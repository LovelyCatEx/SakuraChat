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
import com.lovelycatv.lark.LarkRestClient
import com.lovelycatv.lark.message.LarkTextMessage
import com.lovelycatv.sakurachat.adapters.thirdparty.message.MessageAdapterManager
import com.lovelycatv.sakurachat.core.SakuraChatMessageExtra
import com.lovelycatv.sakurachat.thirdparty.AbstractThirdPartyMessageDispatcher
import com.lovelycatv.sakurachat.types.ThirdPartyPlatform
import com.lovelycatv.sakurachat.utils.toJSONString
import com.lovelycatv.vertex.log.logger
import org.springframework.stereotype.Component

@Component
class LarkMessageDispatcher(
    private val objectMapper: ObjectMapper,
    private val messageAdapterManager: MessageAdapterManager
) : AbstractThirdPartyMessageDispatcher(ThirdPartyPlatform.LARK) {
    private val logger = logger()

    override suspend fun doHandle(vararg args: Any?): Boolean {
        val larkRestClient = firstInstanceOrNull<LarkRestClient>()
            ?: throw IllegalArgumentException("Could not dispatch unsupported event received from lark " +
                    "caused by ${LarkRestClient::class.qualifiedName} not found in args."
            )

        return firstInstanceOrNull<P2MessageReceiveV1>(*args)?.let {
            handleMessageReceivedEvent(larkRestClient, it)
        } ?: firstInstanceOrNull<P2MessageReadV1>(*args)?.let {
            handleMessageReadEvent(larkRestClient, it)
        } ?: throw IllegalArgumentException("Could not dispatch unsupported event received from lark," +
                " args: ${args.joinToString()}"
        )
    }

    private suspend fun handleMessageReceivedEvent(client: LarkRestClient, event: P2MessageReceiveV1): Boolean {
        val rawMessage = event.event.message

        messageAdapterManager
            .getAdapter(ThirdPartyPlatform.LARK)
            ?.transform(
                input = event.event.message,
                extraBody = SakuraChatMessageExtra(
                    platform = ThirdPartyPlatform.LARK,
                    platformAccountId = "",
                    platformInvoker = client
                )
            )
        if (rawMessage.messageType == "text") {
            val message = objectMapper.readValue(rawMessage.content, LarkTextMessage::class.java)


        } else {
            logger.warn("Lark message received but not support by SakuraChat, " +
                    "message type ${rawMessage.messageType}, data: ${rawMessage.toJSONString()}")
        }

        return true
    }

    private suspend fun handleMessageReadEvent(client: LarkRestClient, event: P2MessageReadV1): Boolean {
        return true
    }
}