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
import com.lovelycatv.sakurachat.core.im.message.ChainMessage
import com.lovelycatv.sakurachat.core.im.message.IMessageAdapter
import com.lovelycatv.sakurachat.core.im.message.TextMessage
import com.lovelycatv.sakurachat.types.ThirdPartyPlatform
import com.lovelycatv.sakurachat.utils.toJSONString
import com.mikuac.shiro.dto.event.message.MessageEvent
import com.mikuac.shiro.enums.MsgTypeEnum
import com.mikuac.shiro.model.ArrayMsg
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
        return if (input.arrayMsg.size > 1) {
            ChainMessage(
                sequence = System.currentTimeMillis(),
                extraBody = extraBody,
                messages = input.arrayMsg.mapIndexed { index, msg ->
                    transformArrayMsg(index.toLong(), msg)
                }
            )
        } else {
            TextMessage(
                sequence = System.currentTimeMillis(),
                extraBody = extraBody,
                message = input.message
            )
        }
    }

    private fun transformArrayMsg(index: Long, arrayMsg: ArrayMsg): AbstractMessage {
        return when (arrayMsg.type) {
            MsgTypeEnum.at -> TODO()
            MsgTypeEnum.text -> TextMessage(
                sequence = index,
                extraBody = null,
                message = arrayMsg.data[""]
            )
            MsgTypeEnum.face -> TextMessage(
                sequence = index,
                extraBody = null,
                message = arrayMsg.toJSONString()
            )
            MsgTypeEnum.mface -> TODO()
            MsgTypeEnum.marketface -> TODO()
            MsgTypeEnum.basketball -> TODO()
            MsgTypeEnum.record -> TODO()
            MsgTypeEnum.video -> TODO()
            MsgTypeEnum.rps -> TODO()
            MsgTypeEnum.new_rps -> TODO()
            MsgTypeEnum.dice -> TODO()
            MsgTypeEnum.new_dice -> TODO()
            MsgTypeEnum.shake -> TODO()
            MsgTypeEnum.anonymous -> TODO()
            MsgTypeEnum.share -> TODO()
            MsgTypeEnum.contact -> TODO()
            MsgTypeEnum.location -> TODO()
            MsgTypeEnum.music -> TODO()
            MsgTypeEnum.image -> TODO()
            MsgTypeEnum.reply -> TODO()
            MsgTypeEnum.redbag -> TODO()
            MsgTypeEnum.poke -> TODO()
            MsgTypeEnum.gift -> TODO()
            MsgTypeEnum.forward -> TODO()
            MsgTypeEnum.markdown -> TODO()
            MsgTypeEnum.keyboard -> TODO()
            MsgTypeEnum.node -> TODO()
            MsgTypeEnum.xml -> TODO()
            MsgTypeEnum.json -> TODO()
            MsgTypeEnum.cardimage -> TODO()
            MsgTypeEnum.tts -> TODO()
            MsgTypeEnum.longmsg -> TODO()
            MsgTypeEnum.unknown -> TODO()
        }
    }
}