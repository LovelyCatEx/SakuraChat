/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.lark.utils

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.lovelycatv.lark.message.AbstractLarkMessage
import com.lovelycatv.lark.message.LarkMessageType
import com.lovelycatv.lark.message.LarkTextMessage

object LarkMessageUtils {
    val objectMapper = jacksonObjectMapper()

    fun parseFromJSONString(
        typeName: String,
        jsonString: String,
        objectMapper: ObjectMapper = LarkMessageUtils.objectMapper,
        downgrade: (() -> AbstractLarkMessage)? = null
    ): AbstractLarkMessage {
        val type = LarkMessageType.Companion.getTypeByName(typeName)
            ?: throw IllegalArgumentException("Lark message type $typeName not supported")

        return when (type) {
            LarkMessageType.TEXT -> {
                objectMapper.readValue(jsonString, LarkTextMessage::class.java)
            }

            else -> downgrade?.invoke()
                ?: throw IllegalArgumentException("Lark message type $typeName not supported")
        }
    }
}