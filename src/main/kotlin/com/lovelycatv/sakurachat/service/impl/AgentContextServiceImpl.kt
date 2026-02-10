/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.service.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.lovelycatv.sakurachat.core.SakuraChatAgent
import com.lovelycatv.sakurachat.core.SakuraChatUser
import com.lovelycatv.sakurachat.core.im.message.AbstractMessage
import com.lovelycatv.sakurachat.core.im.message.ChainMessage
import com.lovelycatv.sakurachat.core.im.message.JsonMessage
import com.lovelycatv.sakurachat.core.im.message.QuoteMessage
import com.lovelycatv.sakurachat.core.im.message.TextMessage
import com.lovelycatv.sakurachat.service.AgentContextService
import com.lovelycatv.sakurachat.service.AgentService
import com.lovelycatv.sakurachat.service.ChannelMessageSerializationService
import com.lovelycatv.sakurachat.service.IMChannelMessageService
import com.lovelycatv.sakurachat.types.ChannelMemberType
import com.lovelycatv.vertex.ai.openai.ChatMessageRole
import com.lovelycatv.vertex.ai.openai.message.ChatMessage
import com.lovelycatv.vertex.log.logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

@Service
class AgentContextServiceImpl(
    private val IMChannelMessageService: IMChannelMessageService,
    private val agentService: AgentService,
    private val objectMapper: ObjectMapper,
    private val channelMessageSerializationService: ChannelMessageSerializationService,
) : AgentContextService {
    private val logger = logger()

    override suspend fun getContextForChatCompletions(
        userId: Long,
        agentId: Long,
        channelId: Long
    ): List<ChatMessage> {
        val agent = withContext(Dispatchers.IO) {
            agentService.getRepository().findById(agentId)
        }.getOrNull()

        if (agent == null) {
            logger.error("Could build context for chat completions, because agent with id $agentId not found")
            throw IllegalArgumentException("Agent with id $agentId not found")
        }

        val messages = withContext(Dispatchers.IO) {
            IMChannelMessageService
                .getRepository()
                .findByChannelId(
                    channelId,
                    PageRequest.of(
                        0,
                        128,
                        Sort.Direction.DESC,
                        "createdTime"
                    )
                )
        }.reversed()

        return listOf(
            ChatMessage(
                role = ChatMessageRole.SYSTEM,
                content = agent.prompt
            )
        ) + messages.map {
            this.buildChatMessageFromAbstractMessage(
                message = channelMessageSerializationService.fromJSONString(it.content),
                role = if (it.getRealMemberType() == ChannelMemberType.AGENT) {
                    ChatMessageRole.ASSISTANT
                } else if (it.getRealMemberType() == ChannelMemberType.USER) {
                    ChatMessageRole.USER
                } else {
                    throw IllegalStateException("Unknown memberId ${it.memberId} of channel ${it.channelId}, neither agent nor user.")
                },
            )
        }
    }

    override fun buildChatMessageFromAbstractMessage(
        message: AbstractMessage,
        role: ChatMessageRole
    ): ChatMessage {
        return if (message is ChainMessage) {
            buildChatMessageFromChainMessage(message, role)
        } else {
            buildChatMessageFromAtomicMessage(message, role)
        }
    }

    private fun buildChatMessageFromChainMessage(
        chainMessage: ChainMessage,
        role: ChatMessageRole
    ): ChatMessage {
        return ChatMessage(
            role = role,
            content = chainMessage.messages.map {
                buildChatMessageFromAtomicMessage(it, role)
            }.joinToString(separator = " ", prefix = "", postfix = "") {
                it.content
            }
        )
    }

    private fun buildChatMessageFromAtomicMessage(
        atomicMessage: AbstractMessage,
        role: ChatMessageRole
    ): ChatMessage {
        if (!atomicMessage.isAtomic()) {
            throw IllegalArgumentException("Message ${atomicMessage.type} must be atomic")
        }

        return when (atomicMessage) {
            is TextMessage -> ChatMessage(
                role = role,
                content = atomicMessage.message
            )

            is QuoteMessage -> {
                val raw = buildChatMessageFromAbstractMessage(atomicMessage.message, role)
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
    }
}