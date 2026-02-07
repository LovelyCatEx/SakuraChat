/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.adapters.thirdparty.message

import com.lark.oapi.service.im.v1.model.EventMessage
import com.lovelycatv.lark.message.LarkMessageUtils
import com.lovelycatv.lark.message.LarkTextMessage
import com.lovelycatv.sakurachat.core.ExtraBody
import com.lovelycatv.sakurachat.core.im.message.AbstractMessage
import com.lovelycatv.sakurachat.core.im.message.IMessageAdapter
import com.lovelycatv.sakurachat.core.im.message.TextMessage
import com.lovelycatv.sakurachat.types.ThirdPartyPlatform
import com.lovelycatv.sakurachat.utils.toJSONString
import org.springframework.stereotype.Component

@Component
class LarkMessageAdapter : IMessageAdapter<EventMessage> {
    override fun getPlatform(): ThirdPartyPlatform {
        return ThirdPartyPlatform.LARK
    }

    override fun getInputMessageClass(): Class<EventMessage> {
        return EventMessage::class.java
    }

    override fun transform(input: EventMessage, extraBody: ExtraBody): AbstractMessage {
        val larkMessage = LarkMessageUtils.parseFromJSONString(
            input.messageType,
            input.content
        ) {
            LarkTextMessage(input.content)
        }

        return TextMessage(
            sequence = System.currentTimeMillis(),
            message = if (larkMessage is LarkTextMessage) {
                larkMessage.text
            } else {
                larkMessage.toJSONString()
            },
            extraBody = extraBody
        )
    }
}