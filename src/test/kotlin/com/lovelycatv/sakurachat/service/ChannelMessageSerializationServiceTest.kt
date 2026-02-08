/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.service

import com.lovelycatv.sakurachat.core.SakuraChatMessageExtra
import com.lovelycatv.sakurachat.core.im.message.ChainMessage
import com.lovelycatv.sakurachat.core.im.message.TextMessage
import com.lovelycatv.sakurachat.service.impl.ChannelMessageSerializationServiceImpl
import com.lovelycatv.sakurachat.types.ThirdPartyPlatform
import com.lovelycatv.sakurachat.utils.toJSONString
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ChannelMessageSerializationServiceTest {
    private val channelMessageSerializationService = ChannelMessageSerializationServiceImpl()

    val chainMessage = ChainMessage(
        sequence = System.currentTimeMillis(),
        extraBody = SakuraChatMessageExtra(
            platform = ThirdPartyPlatform.NAPCAT_OICQ,
            platformAccountId = "",
            platformInvoker = ""
        ),
        messages = listOf(
            ChainMessage(
                sequence = 0,
                extraBody = null,
                messages = listOf(
                    TextMessage(
                        sequence = 0,
                        extraBody = null,
                        message = "Hello"
                    ),
                    TextMessage(
                        sequence = 1,
                        extraBody = null,
                        message = "World"
                    )
                )
            ),
            TextMessage(
                sequence = 1,
                extraBody = null,
                message = "Hello World"
            ),
            TextMessage(
                sequence = 2,
                extraBody = null,
                message = "Hello World"
            )
        )
    )

    @Test
    fun toJSONString() {
        val expect = "{\"type\":\"chain\",\"messages\":[\"{\\\"type\\\":\\\"chain\\\",\\\"messages\\\":[\\\"{\\\\\\\"message\\\\\\\":\\\\\\\"Hello\\\\\\\",\\\\\\\"type\\\\\\\":\\\\\\\"TEXT\\\\\\\"}\\\",\\\"{\\\\\\\"message\\\\\\\":\\\\\\\"World\\\\\\\",\\\\\\\"type\\\\\\\":\\\\\\\"TEXT\\\\\\\"}\\\"]}\",\"{\\\"message\\\":\\\"Hello World\\\",\\\"type\\\":\\\"TEXT\\\"}\",\"{\\\"message\\\":\\\"Hello World\\\",\\\"type\\\":\\\"TEXT\\\"}\"]}"
        val actual = channelMessageSerializationService.toJSONString(chainMessage)

        println(actual)
        assertEquals(expect, actual)
    }

    @Test
    fun fromJSONString() {
        val jsonString = "{\"messages\":[{\"messages\":[{\"message\":\"Hello\",\"type\":\"TEXT\"},{\"message\":\"World\",\"type\":\"TEXT\"}],\"type\":\"CHAIN\"},{\"message\":\"Hello World\",\"type\":\"TEXT\"},{\"message\":\"Hello World\",\"type\":\"TEXT\"}],\"type\":\"CHAIN\"}"
        val actual = channelMessageSerializationService.fromJSONString(jsonString) as ChainMessage

        println("Original: $jsonString")
        println("Parsed: ${actual.toJSONString()}")

        assertEquals(chainMessage.messages.size, actual.messages.size)
        assertEquals((chainMessage.messages[0] as ChainMessage).messages.size, (actual.messages[0] as ChainMessage).messages.size)
        assertEquals(((chainMessage.messages[0] as ChainMessage).messages[0] as TextMessage).message, ((actual.messages[0] as ChainMessage).messages[0] as TextMessage).message)
    }

}