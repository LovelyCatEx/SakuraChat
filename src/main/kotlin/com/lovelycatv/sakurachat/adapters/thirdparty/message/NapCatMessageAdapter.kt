/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.adapters.thirdparty.message

import com.lovelycatv.sakurachat.core.ExtraBody
import com.lovelycatv.sakurachat.core.im.message.AbstractMessage
import com.lovelycatv.sakurachat.core.im.message.IMessageAdapter
import com.lovelycatv.sakurachat.core.im.message.TextMessage
import com.lovelycatv.sakurachat.types.ThirdPartyPlatform
import com.mikuac.shiro.dto.event.message.MessageEvent
import org.springframework.stereotype.Component

@Component
class NapCatMessageAdapter : IMessageAdapter<MessageEvent> {
    override fun getPlatform(): ThirdPartyPlatform {
        return ThirdPartyPlatform.NAPCAT_OICQ
    }

    override fun getInputMessageClass(): Class<MessageEvent> {
        return MessageEvent::class.java
    }

    override fun transform(input: MessageEvent, extraBody: ExtraBody): AbstractMessage {
        return TextMessage(
            sequence = System.currentTimeMillis(),
            message = input.message,
            extraBody = extraBody
        )
    }
}