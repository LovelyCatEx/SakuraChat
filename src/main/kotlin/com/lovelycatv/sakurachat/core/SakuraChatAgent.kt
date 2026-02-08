/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.core

import com.lovelycatv.sakurachat.core.im.message.AbstractMessage
import com.lovelycatv.sakurachat.core.im.message.ChainMessage
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
                getMessageToResponse(sender, message).forEach {
                    if (it.isNotEmpty()) {
                        channel.sendPrivateMessage(
                            channel.getAgentMember(agent.agent.id!!)!!,
                            sender,
                            it
                        )
                    }
                }
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
                getMessageToResponse(sender, message).forEach {
                    if (it.isNotEmpty()) {
                        channel.sendGroupMessage(
                            channel.getAgentMember(agent.agent.id!!)!!,
                            it
                        )
                    }
                }
            }
        }
    }

    private suspend fun getMessageToResponse(
        sender: SakuraChatUser,
        message: AbstractMessage
    ): List<AbstractMessage> {
        val chatModelEntity = agent.chatModel ?: return listOf(message)
        val modelCredential = agent.chatModel.credential ?: return listOf(message)
        val modelProvider = agent.chatModel.provider ?: return listOf(message)

        val predictedPoints = (
                0.95 * agent.agent.prompt.length * chatModelEntity.chatModel.getQualifiedTokenPointRate(chatModelEntity.chatModel.inputTokenPointRate) +
                chatModelEntity.chatModel.maxTokens * chatModelEntity.chatModel.getQualifiedTokenPointRate(chatModelEntity.chatModel.outputTokenPointRate)
        ).toLong()

        val pointsThreshold = userService.hasPoints(sender.user.id!!, predictedPoints)

        if (!pointsThreshold) {
            return listOf(
                TextMessage(
                    sequence = System.currentTimeMillis(),
                    message = "Insufficient points, required $predictedPoints",
                    extraBody = message.extraBody
                )
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

        val resp = try {
            client.chatCompletionBlocking(
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
        } catch (e: Exception) {
            logger.error("An error occurred when calling chat completion request, chatModel: ${agent.chatModel.toJSONString()}", e)
            null
        }

        if (resp == null) {
            return listOf(
                TextMessage(
                    sequence = System.currentTimeMillis(),
                    message = "Could not process your request, please try again later",
                    extraBody = message.extraBody
                )
            )
        }

        val consumedPoints = resp.usage.promptTokens * chatModelEntity.chatModel.getQualifiedTokenPointRate(chatModelEntity.chatModel.inputTokenPointRate) +
                resp.usage.completionTokens * chatModelEntity.chatModel.getQualifiedTokenPointRate(chatModelEntity.chatModel.outputTokenPointRate)

        userService.consumePoints(sender.user.id!!, ceil(consumedPoints).toLong())

        return resp.choices.first().message.content.split("\n").map {
            TextMessage(
                sequence = System.currentTimeMillis(),
                message = it,
                extraBody = message.extraBody
            )
        }
    }
}