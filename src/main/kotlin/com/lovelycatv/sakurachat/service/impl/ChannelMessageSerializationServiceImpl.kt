/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.service.impl

import com.fasterxml.jackson.annotation.JsonSetter
import com.fasterxml.jackson.annotation.Nulls
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.lovelycatv.sakurachat.core.im.message.AbstractMessage
import com.lovelycatv.sakurachat.service.ChannelMessageSerializationService
import org.springframework.stereotype.Service

@Service
class ChannelMessageSerializationServiceImpl : ChannelMessageSerializationService {
    private val objectMapper = jacksonObjectMapper().apply {
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
    }

    override fun toJSONString(abstractMessage: AbstractMessage): String {
        @Suppress("UNCHECKED_CAST")
        val messageMap = objectMapper.convertValue(abstractMessage, Map::class.java) as MutableMap<String, *>

        messageMap.remove("sequence")
        messageMap.remove("extraBody")

        return objectMapper.writeValueAsString(messageMap)
    }

    override fun fromJSONString(jsonString: String): AbstractMessage {
        return objectMapper.readValue(jsonString, AbstractMessage::class.java)
    }
}