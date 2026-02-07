/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.thirdparty.lark

import com.fasterxml.jackson.databind.ObjectMapper
import com.lark.oapi.service.im.v1.model.P2MessageReadV1
import com.lark.oapi.service.im.v1.model.P2MessageReceiveV1
import com.lovelycatv.sakurachat.thirdparty.AbstractThirdPartyMessageDispatcher
import com.lovelycatv.sakurachat.types.ThirdPartyPlatform
import com.lovelycatv.sakurachat.utils.toJSONString
import com.lovelycatv.vertex.log.logger
import org.springframework.stereotype.Component

@Component
class LarkMessageDispatcher(
    private val objectMapper: ObjectMapper,
) : AbstractThirdPartyMessageDispatcher(ThirdPartyPlatform.LARK) {
    private val logger = logger()

    override suspend fun doHandle(vararg args: Any?): Boolean {
        firstInstanceOrNull<P2MessageReceiveV1>(*args)?.let {
            handleMessageReceivedEvent(it)
        }

        firstInstanceOrNull<P2MessageReadV1>(*args)?.let {
            handleMessageReadEvent(it)
        }

        return true
    }

    private suspend fun handleMessageReceivedEvent(event: P2MessageReceiveV1) {
        val rawMessage = event.event.message

        if (rawMessage.messageType == "text") {
            val message = objectMapper.readValue(rawMessage.content, LarkTextMessage::class.java)


        } else {
            logger.warn("Lark message received but not support by SakuraChat, " +
                    "message type ${rawMessage.messageType}, data: ${rawMessage.toJSONString()}")
        }

    }

    private suspend fun handleMessageReadEvent(event: P2MessageReadV1) {

    }
}