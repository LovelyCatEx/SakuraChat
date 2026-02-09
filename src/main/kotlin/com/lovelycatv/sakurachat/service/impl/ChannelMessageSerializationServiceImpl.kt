/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.service.impl

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.lovelycatv.sakurachat.core.im.message.AbstractMessage
import com.lovelycatv.sakurachat.core.im.message.ChainMessage
import com.lovelycatv.sakurachat.service.ChannelMessageSerializationService
import org.springframework.stereotype.Service

@Service
class ChannelMessageSerializationServiceImpl : ChannelMessageSerializationService {
    private val objectMapper = jacksonObjectMapper().apply {
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
    }

    override fun toJSONString(abstractMessage: AbstractMessage): String {
        return when (abstractMessage) {
            is ChainMessage -> chainMessageToJson(abstractMessage)
            else -> singleMessageToJson(abstractMessage)
        }
    }

    private fun singleMessageToJson(message: AbstractMessage): String {
        val jsonNode = objectMapper.valueToTree<JsonNode>(message)

        (jsonNode as ObjectNode).apply {
            remove("sequence")
            remove("extraBody")
        }

        return objectMapper.writeValueAsString(jsonNode)
    }

    private fun chainMessageToJson(chainMessage: ChainMessage): String {
        // 收集消息对象（JsonNode）
        val messageNodes = chainMessage.messages.map { message ->
            when (message) {
                is ChainMessage -> {
                    // 递归处理嵌套 ChainMessage，返回 JsonNode
                    objectMapper.readTree(chainMessageToJson(message))
                }
                else -> {
                    // 普通消息转为 JsonNode 并移除字段
                    val jsonNode = objectMapper.valueToTree<JsonNode>(message)
                    (jsonNode as ObjectNode).apply {
                        remove("sequence")
                        remove("extraBody")
                    }
                    jsonNode
                }
            }
        }

        // 构建 ChainMessage 的 JSON 结构
        val result = objectMapper.createObjectNode().apply {
            // 添加 type 字段
            put("type", chainMessage.type.name)

            // 添加 messages 数组，包含真正的对象而不是字符串
            val messagesArray = putArray("messages")
            messageNodes.forEach { node ->
                messagesArray.add(node)
            }

            // 如果需要，也可以移除 ChainMessage 本身的 sequence 和 extraBody
            //（如果 ChainMessage 也有这些字段）
            remove("sequence")
            remove("extraBody")
        }

        return objectMapper.writeValueAsString(result)
    }

    override fun fromJSONString(jsonString: String): AbstractMessage {
        return objectMapper.readValue(jsonString, AbstractMessage::class.java)
    }
}