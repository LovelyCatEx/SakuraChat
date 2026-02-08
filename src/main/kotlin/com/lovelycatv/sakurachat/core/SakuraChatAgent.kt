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
import com.lovelycatv.vertex.ai.openai.response.ChatCompletionStreamChunkResponse
import com.lovelycatv.vertex.log.logger
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.ceil
import kotlin.math.log

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
                getMessageToResponse(sender, message, true) {
                    it.forEach {
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
    }

    override fun onGroupMessage(
        channel: SakuraChatMessageChannel,
        sender: ISakuraChatMessageChannelMember,
        message: AbstractMessage
    ) {
        println("Agent ${agent.agent.id} received a group message: ${message.toJSONString()}")

        coroutineScope.launch {
            if (sender is SakuraChatUser) {
                getMessageToResponse(sender, message, true) {
                    if (it.isNotEmpty()) {
                        it.forEach {
                            channel.sendGroupMessage(
                                channel.getAgentMember(agent.agent.id!!)!!,
                                it
                            )
                        }
                    }
                }
            }
        }
    }

    private suspend fun getMessageToResponse(
        sender: SakuraChatUser,
        message: AbstractMessage,
        stream: Boolean,
        emitter: suspend (List<AbstractMessage>) -> Unit
    ) {
        val chatModelEntity = agent.chatModel ?: return emitter.invoke(listOf(message))
        val modelCredential = agent.chatModel.credential ?: return emitter.invoke(listOf(message))
        val modelProvider = agent.chatModel.provider ?: return emitter.invoke(listOf(message))

        val predictedPoints = (
                0.95 * agent.agent.prompt.length * chatModelEntity.chatModel.getQualifiedTokenPointRate(chatModelEntity.chatModel.inputTokenPointRate) +
                chatModelEntity.chatModel.maxTokens * chatModelEntity.chatModel.getQualifiedTokenPointRate(chatModelEntity.chatModel.outputTokenPointRate)
        ).toLong()

        val pointsThreshold = userService.hasPoints(sender.user.id!!, predictedPoints)

        if (!pointsThreshold) {
            return emitter.invoke(
                listOf(
                    TextMessage(
                        sequence = System.currentTimeMillis(),
                        message = "Insufficient points, required $predictedPoints",
                        extraBody = message.extraBody
                    )
                )
            )
        }

        val client = VertexAIClient(
            config = VertexAIClientConfig(
                baseUrl = modelProvider.chatCompletionsUrl.replace("chat/completions", ""),
                apiKey = modelCredential.data,
                timeoutSeconds = 30,
                enableLogging = true
            )
        )

        val chatCompletionRequest = ChatCompletionRequest(
            model = chatModelEntity.chatModel.qualifiedName,
            maxTokens = chatModelEntity.chatModel.maxTokens,
            stream = stream,
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
            temperature = chatModelEntity.chatModel.getQualifiedTemperature(),
            thinking = ChatCompletionRequest.ThinkingParameter(
                type = "disabled"
            )
        )

        val hasDelimiter = agent.agent.delimiter != null
        val delimiter by lazy {
            agent.agent.delimiter?.unescape()
                ?: throw IllegalStateException("Please check hasDelimiter before using delimiter")
        }

        if (chatCompletionRequest.stream) {
            // Streaming
            val chunks = mutableListOf<ChatCompletionStreamChunkResponse>()
            val resp = try {
                val buffer = StringBuilder()
                client.chatCompletionStreaming(chatCompletionRequest).collect {
                    chunks.add(it)

                    val choiceMessage = it.choices.firstOrNull()?.delta?.content

                    if (choiceMessage != null) {
                        if (hasDelimiter) {
                            if (choiceMessage.contains(delimiter)) {
                                val sp = choiceMessage.split(delimiter)

                                if (sp[0].isNotEmpty()) {
                                    buffer.append(sp[0])
                                }

                                emitter.invoke(
                                    listOf(
                                        TextMessage(
                                            sequence = System.currentTimeMillis(),
                                            message = buffer.toString(),
                                            extraBody = message.extraBody
                                        )
                                    )
                                )

                                buffer.clear()

                                if (sp[1].isNotEmpty()) {
                                    buffer.append(sp[1])
                                }
                            } else {
                                buffer.append(choiceMessage)
                            }
                        } else {
                            // Not delimiter
                            buffer.append(choiceMessage)
                        }
                    } else {
                        // Abort empty choices
                    }
                }

                if (buffer.isNotEmpty()) {
                    emitter.invoke(
                        listOf(
                            TextMessage(
                                sequence = System.currentTimeMillis(),
                                message = buffer.toString(),
                                extraBody = message.extraBody
                            )
                        )
                    )
                }

                chunks.last()
            } catch (e: Exception) {
                logger.error("An error occurred when calling stream chat completion request, chatModel: ${agent.chatModel.toJSONString()}", e)
                null
            }

            if (resp == null) {
                return emitter.invoke(listOf(
                    TextMessage(
                        sequence = System.currentTimeMillis(),
                        message = "Could not process your request, please try again later",
                        extraBody = message.extraBody
                    )
                ))
            }

            // After actions
            if (resp.usage != null) {
                val consumedPoints = resp.usage!!.promptTokens * chatModelEntity.chatModel.getQualifiedTokenPointRate(chatModelEntity.chatModel.inputTokenPointRate) +
                        resp.usage!!.completionTokens * chatModelEntity.chatModel.getQualifiedTokenPointRate(chatModelEntity.chatModel.outputTokenPointRate)

                userService.consumePoints(sender.user.id!!, ceil(consumedPoints).toLong())
            } else {
                logger.error("[ERROR]" + "=".repeat(128))
                logger.error("StreamChatCompletion with model ${chatModelEntity.chatModel.qualifiedName} of provider " +
                        "${chatModelEntity.provider?.name} responses null usages which is disallowed, " +
                        "data: ${chatModelEntity.toJSONString()}"
                )
                logger.error("Collected chunks:")
                chunks.forEach { chunk ->
                    logger.error(chunk.toJSONString())
                }
                logger.error("[ERROR]" + "=".repeat(128))
            }
        } else {
            // Blocking
            val resp = try {
                client.chatCompletionBlocking(chatCompletionRequest)
            } catch (e: Exception) {
                logger.error("An error occurred when calling blocking chat completion request, chatModel: ${agent.chatModel.toJSONString()}", e)
                null
            }

            if (resp == null) {
                return emitter.invoke(listOf(
                    TextMessage(
                        sequence = System.currentTimeMillis(),
                        message = "Could not process your request, please try again later",
                        extraBody = message.extraBody
                    )
                ))
            }

            val choiceMessage = resp.choices.first().message.content

            return emitter.invoke(
                agent.agent.delimiter?.let { delimiter ->
                    choiceMessage.split(delimiter.unescape()).map {
                        TextMessage(
                            sequence = System.currentTimeMillis(),
                            message = it,
                            extraBody = message.extraBody
                        )
                    }
                } ?: listOf(
                    TextMessage(
                        sequence = System.currentTimeMillis(),
                        message = choiceMessage,
                        extraBody = message.extraBody
                    )
                )
            ).also {
                // After actions
                val consumedPoints = resp.usage.promptTokens * chatModelEntity.chatModel.getQualifiedTokenPointRate(chatModelEntity.chatModel.inputTokenPointRate) +
                        resp.usage.completionTokens * chatModelEntity.chatModel.getQualifiedTokenPointRate(chatModelEntity.chatModel.outputTokenPointRate)

                userService.consumePoints(sender.user.id!!, ceil(consumedPoints).toLong())
            }
        }


    }

    private fun String.unescape(): String {
        return this
            .replace("\\n", "\n")
            .replace("\\r", "\r")
            .replace("\\t", "\t")
            .replace("\\\\", "\\")
            .replace("\\\"", "\"")
            .replace("\\'", "'")
    }
}