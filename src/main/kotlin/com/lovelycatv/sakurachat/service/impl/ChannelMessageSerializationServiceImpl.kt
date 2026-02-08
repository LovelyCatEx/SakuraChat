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
        val messageJsons = chainMessage.messages.map { message ->
            when (message) {
                is ChainMessage -> {
                    chainMessageToJson(message)
                }

                else -> {
                    singleMessageToJson(message)
                }
            }
        }

        val result = mapOf(
            "type" to chainMessage.type.name.lowercase(),
            "messages" to messageJsons
        )

        return objectMapper.writeValueAsString(result)
    }

    override fun fromJSONString(jsonString: String): AbstractMessage {
        return objectMapper.readValue(jsonString, AbstractMessage::class.java)
    }
}