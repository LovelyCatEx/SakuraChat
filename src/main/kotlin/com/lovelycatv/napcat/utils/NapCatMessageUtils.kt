/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.napcat.utils

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.lovelycatv.napcat.message.AbstractNapCatMessage
import com.lovelycatv.napcat.message.NapCatFaceMessage
import com.lovelycatv.napcat.message.NapCatImageMessage
import com.lovelycatv.napcat.types.NapCatMessageType
import com.lovelycatv.napcat.message.NapCatPokeMessage
import com.lovelycatv.napcat.message.NapCatRecordMessage
import com.lovelycatv.napcat.message.NapCatReplyMessage
import com.lovelycatv.napcat.message.NapCatUnknownMessage
import com.lovelycatv.napcat.message.NapCatVideoMessage
import com.lovelycatv.sakurachat.utils.toJSONString
import com.mikuac.shiro.model.ArrayMsg

object NapCatMessageUtils {
    private val objectMapper: ObjectMapper = jacksonObjectMapper()

    fun fromArrayMsg(arrayMsg: ArrayMsg): AbstractNapCatMessage {
        val type = NapCatMessageType.Companion.fromMsgTypeEnum(arrayMsg.type)
            ?: throw IllegalArgumentException("ArrayMsg type ${arrayMsg.type.name} not supported yet, " +
                    "already supports: ${NapCatMessageType.entries.joinToString { it.name }}"
            )

        if (type == NapCatMessageType.UNKNOWN) {
            return NapCatUnknownMessage(
                data = arrayMsg.data
            )
        }

        val data = arrayMsg.data.toJSONString()

        return try {
            objectMapper.readValue(
                data,
                when (type) {
                    NapCatMessageType.TEXT -> NapCatImageMessage::class.java
                    NapCatMessageType.FACE -> NapCatFaceMessage::class.java
                    NapCatMessageType.IMAGE -> NapCatImageMessage::class.java
                    NapCatMessageType.VIDEO -> NapCatVideoMessage::class.java
                    NapCatMessageType.RECORD -> NapCatRecordMessage::class.java
                    NapCatMessageType.POKE -> NapCatPokeMessage::class.java
                    NapCatMessageType.REPLY -> NapCatReplyMessage::class.java

                    else -> throw IllegalArgumentException("Unsupported ${AbstractNapCatMessage::class.qualifiedName} type ${type.name}")
                }
            )
        } catch (e: Exception) {
            throw IllegalArgumentException("Could not transform ArrayMsg(${arrayMsg.type.name}) " +
                    "to ${NapCatMessageType::class.qualifiedName}(${type.name}), message: ${e.message}"
            )
        }
    }
}