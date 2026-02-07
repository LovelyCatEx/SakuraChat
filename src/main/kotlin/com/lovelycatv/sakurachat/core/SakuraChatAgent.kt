/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.core

import com.lovelycatv.sakurachat.core.im.message.AbstractMessage
import com.lovelycatv.sakurachat.core.im.message.TextMessage
import com.lovelycatv.sakurachat.entity.aggregated.AggregatedAgentEntity
import com.lovelycatv.sakurachat.service.UserService
import com.lovelycatv.sakurachat.utils.toJSONString
import com.lovelycatv.vertex.ai.openai.ChatMessageRole
import com.lovelycatv.vertex.ai.openai.VertexAIClient
import com.lovelycatv.vertex.ai.openai.VertexAIClientConfig
import com.lovelycatv.vertex.ai.openai.message.ChatMessage
import com.lovelycatv.vertex.ai.openai.message.ChoiceMessage
import com.lovelycatv.vertex.ai.openai.message.IChatMessage
import com.lovelycatv.vertex.ai.openai.request.ChatCompletionRequest
import com.lovelycatv.vertex.log.logger
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.ceil

class SakuraChatAgent(
    val agent: AggregatedAgentEntity,
    val userService: UserService
) : ISakuraChatMessageChannelMember {
    companion object {
        const val MEMBER_PREFIX = "agent_"

        fun buildMemberId(agentId: Long) = "${MEMBER_PREFIX}$agentId"
    }

    private val logger = logger()

    override val memberId: String get() = buildMemberId(this.agent.agent.id!!)

    private val coroutineScope = CoroutineScope(
        Dispatchers.IO + CoroutineName("SakuraChatAgent#$memberId")
    )

    override fun onPrivateMessage(
        channel: SakuraChatMessageChannel,
        sender: ISakuraChatMessageChannelMember,
        message: AbstractMessage
    ) {
        println("Agent ${agent.agent.id} received a private message: ${message.toJSONString()}")

        coroutineScope.launch {
            if (sender is SakuraChatUser) {
                channel.sendPrivateMessage(
                    channel.getAgentMember(agent.agent.id!!)!!,
                    sender,
                    getMessageToResponse(sender, message)
                )
            }
        }
    }

    override fun onGroupMessage(
        channel: SakuraChatMessageChannel,
        sender: ISakuraChatMessageChannelMember,
        message: AbstractMessage
    ) {
        println("Agent ${agent.agent.id} received a group message: ${message.toJSONString()}")

        coroutineScope.launch {
            if (sender is SakuraChatUser) {
                channel.sendGroupMessage(
                    channel.getAgentMember(agent.agent.id!!)!!,
                    getMessageToResponse(sender, message)
                )
            }
        }
    }

    private suspend fun getMessageToResponse(
        sender: SakuraChatUser,
        message: AbstractMessage
    ): AbstractMessage {
        val chatModelEntity = agent.chatModel ?: return message
        val modelCredential = agent.chatModel.credential ?: return message
        val modelProvider = agent.chatModel.provider ?: return message

        val predictedPoints = (
                (0.95 * agent.agent.prompt.length + chatModelEntity.chatModel.maxTokens)
                        * chatModelEntity.chatModel.getQualifiedTokenPointRate()
        ).toLong()

        val pointsThreshold = userService.hasPoints(sender.user.id!!, predictedPoints)

        if (!pointsThreshold) {
            return TextMessage(
                sequence = System.currentTimeMillis(),
                message = "Insufficient points, required $predictedPoints",
                extraBody = message.extraBody
            )
        }

        val client = VertexAIClient(
            config = VertexAIClientConfig(
                baseUrl = modelProvider.chatCompletionsUrl.replace("chat/completions", ""),
                apiKey = modelCredential.data,
                timeoutSeconds = 60,
                enableLogging = true
            )
        )

        val resp = client.chatCompletionBlocking(
            ChatCompletionRequest(
                model = chatModelEntity.chatModel.qualifiedName,
                maxTokens = chatModelEntity.chatModel.maxTokens,
                stream = false,
                messages = listOf(
                    ChatMessage(
                        role = ChatMessageRole.SYSTEM,
                        content = agent.agent.prompt
                    ),
                    ChatMessage(
                        role = ChatMessageRole.USER,
                        content = message.toJSONString()
                    )
                ),
                temperature = chatModelEntity.chatModel.getQualifiedTemperature()
            )
        )

        val consumedPoints = resp.usage.totalTokens * chatModelEntity.chatModel.getQualifiedTokenPointRate()

        userService.consumePoints(sender.user.id!!, ceil(consumedPoints).toLong())

        return TextMessage(
            sequence = System.currentTimeMillis(),
            message = resp.choices.first().message.content,
            extraBody = message.extraBody
        )
    }
}