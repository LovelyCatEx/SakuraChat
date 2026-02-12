/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.service.impl

import com.lovelycatv.sakurachat.core.im.message.*
import com.lovelycatv.sakurachat.entity.channel.IMChannelMessageEntity
import com.lovelycatv.sakurachat.repository.AgentChannelRelationRepository
import com.lovelycatv.sakurachat.repository.UserChannelRelationRepository
import com.lovelycatv.sakurachat.service.*
import com.lovelycatv.sakurachat.types.ChannelMemberType
import com.lovelycatv.sakurachat.types.ChannelType
import com.lovelycatv.vertex.ai.openai.ChatMessageRole
import com.lovelycatv.vertex.ai.openai.message.ChatMessage
import com.lovelycatv.vertex.log.logger
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

@Service
class AgentContextServiceImpl(
    private val imChannelMessageService: IMChannelMessageService,
    private val imChannelService: IMChannelService,
    private val agentService: AgentService,
    private val channelMessageSerializationService: ChannelMessageSerializationService,
    private val agentChannelRelationRepository: AgentChannelRelationRepository,
    private val userChannelRelationRepository: UserChannelRelationRepository,
) : AgentContextService {
    private val logger = logger()

    override fun getContextForChatCompletions(
        userId: Long,
        agentId: Long,
        channelId: Long
    ): List<ChatMessage> {
        val agent = agentService.getRepository().findById(agentId).getOrNull()

        if (agent == null) {
            logger.error("Could build context for chat completions, because agent with id $agentId not found")
            throw IllegalArgumentException("Agent with id $agentId not found")
        }

        val channel = imChannelService.getByIdOrThrow(channelId)

        val prompt = listOf(
            ChatMessage(
                role = ChatMessageRole.SYSTEM,
                content = agent.prompt + """
                    If a user message starts with [?], it means you are now conversing with a different user.  
                    The text inside the brackets [] is the unique identifier of that user.

                    You must primarily respond based on the message from the user you are currently speaking with: **[$userId]**.  
                    Messages from other users may be used as contextual reference only.

                    **Important: You are only allowed to reply to the message from user [$userId].**
                    **Do not respond to messages from any other users.**
                    **Important: You are only allowed to reply to the message from user [$userId].**
                    **Do not respond to messages from any other users.**
                    **Important: You are only allowed to reply to the message from user [$userId].**
                    **Do not respond to messages from any other users.**
                """.trimIndent()
            )
        )

        val messagesInChannel = imChannelMessageService
            .getRepository()
            .findByChannelId(
                channelId,
                PageRequest.of(
                    0,
                    128,
                    Sort.Direction.DESC,
                    "createdTime"
                )
            ).reversed()

        val contextBody = if (channel.getRealChannelType() == ChannelType.PRIVATE) {
            this.processPrivateMessages(messagesInChannel)
        } else if (channel.getRealChannelType() == ChannelType.GROUP) {
            this.processGroupMessages(agentId, messagesInChannel)
        } else {
            throw IllegalArgumentException("Unsupported channel type ${channel.getRealChannelType().name}")
        }

        return prompt + contextBody
    }

    private fun processPrivateMessages(
        messages: List<IMChannelMessageEntity>
    ): List<ChatMessage> {
        return messages.map {
            this.buildChatMessageFromAbstractMessage(
                message = channelMessageSerializationService.fromJSONString(it.content),
                role = if (it.getRealMemberType() == ChannelMemberType.AGENT) {
                    ChatMessageRole.ASSISTANT
                } else if (it.getRealMemberType() == ChannelMemberType.USER) {
                    ChatMessageRole.USER
                } else {
                    throw IllegalStateException("Unknown memberId ${it.memberId} of channel ${it.channelId}, neither agent nor user.")
                },
                senderIdentifier = null
            )
        }
    }

    private fun processGroupMessages(agentId: Long, messages: List<IMChannelMessageEntity>): List<ChatMessage> {
        return messages.map {
            val role = if (it.getRealMemberType() == ChannelMemberType.AGENT) {
                // the message may be sent by other AGENT in group though the memberType is AGENT
                if (it.memberId == agentId) {
                    // only when the memberId is the current agentId
                    ChatMessageRole.ASSISTANT
                } else {
                    // otherwise the role should be recognized as USER
                    ChatMessageRole.USER
                }
            } else if (it.getRealMemberType() == ChannelMemberType.USER) {
                ChatMessageRole.USER
            } else {
                throw IllegalStateException("Unknown memberId ${it.memberId} of channel ${it.channelId}, neither agent nor user.")
            }

            this.buildChatMessageFromAbstractMessage(
                message = channelMessageSerializationService.fromJSONString(it.content),
                role = role,
                senderIdentifier = if (role == ChatMessageRole.USER)
                    "[${it.memberId}]"
                else
                    null
            )
        }
    }

    override fun buildChatMessageFromAbstractMessage(
        message: AbstractMessage,
        role: ChatMessageRole,
        senderIdentifier: String?
    ): ChatMessage {
        return if (message is ChainMessage) {
            buildChatMessageFromChainMessage(message, role, senderIdentifier)
        } else {
            buildChatMessageFromAtomicMessage(message, role, senderIdentifier)
        }
    }

    private fun buildChatMessageFromChainMessage(
        chainMessage: ChainMessage,
        role: ChatMessageRole,
        senderIdentifier: String?
    ): ChatMessage {
        return ChatMessage(
            role = role,
            content = chainMessage.messages.map {
                buildChatMessageFromAtomicMessage(it, role, senderIdentifier)
            }.joinToString(separator = " ", prefix = "", postfix = "") {
                it.content
            }
        )
    }

    private fun buildChatMessageFromAtomicMessage(
        atomicMessage: AbstractMessage,
        role: ChatMessageRole,
        senderIdentifier: String?
    ): ChatMessage {
        if (!atomicMessage.isAtomic()) {
            throw IllegalArgumentException("Message ${atomicMessage.type} must be atomic")
        }

        val raw = when (atomicMessage) {
            is TextMessage -> ChatMessage(
                role = role,
                content = atomicMessage.message
            )

            is QuoteMessage -> {
                val raw = buildChatMessageFromAbstractMessage(atomicMessage.message, role, senderIdentifier)
                raw.copy(
                    content = "[quote:${raw.content}]"
                )
            }

            is JsonMessage -> ChatMessage(
                role = role,
                content = atomicMessage.jsonString
            )

            else -> throw IllegalArgumentException("Unsupported message type ${atomicMessage.javaClass}")
        }

        return if (senderIdentifier != null) {
           raw.copy(content = senderIdentifier + raw.content)
        } else {
            raw
        }
    }
}