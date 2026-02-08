/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.adapters.thirdparty.message

import com.lovelycatv.napcat.message.NapCatFaceMessage
import com.lovelycatv.napcat.message.NapCatReplyMessage
import com.lovelycatv.napcat.message.NapCatTextMessage
import com.lovelycatv.napcat.utils.NapCatMessageUtils
import com.lovelycatv.sakurachat.core.ExtraBody
import com.lovelycatv.sakurachat.core.im.message.*
import com.lovelycatv.sakurachat.repository.NapCatGroupMessageRepository
import com.lovelycatv.sakurachat.repository.NapCatPrivateMessageRepository
import com.lovelycatv.sakurachat.types.ThirdPartyPlatform
import com.mikuac.shiro.common.utils.ShiroUtils
import com.mikuac.shiro.dto.event.message.MessageEvent
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent
import com.mikuac.shiro.model.ArrayMsg
import org.springframework.stereotype.Component

@Component
class NapCatMessageAdapter(
    private val napCatPrivateMessageRepository: NapCatPrivateMessageRepository,
    private val napCatGroupMessageRepository: NapCatGroupMessageRepository
) : IMessageAdapter<MessageEvent> {
    override fun getPlatform(): ThirdPartyPlatform {
        return ThirdPartyPlatform.NAPCAT_OICQ
    }

    override fun getInputMessageClass(): Class<MessageEvent> {
        return MessageEvent::class.java
    }

    override fun transform(input: MessageEvent, extraBody: ExtraBody): AbstractMessage {
        val isPrivateMsg = input is PrivateMessageEvent

        return transformArrayMsgList(isPrivateMsg, input.arrayMsg, extraBody)
    }

    private fun transformArrayMsgList(isPrivateMsg: Boolean, arrayMsg: List<ArrayMsg>, extraBody: ExtraBody?): AbstractMessage {
        return if (arrayMsg.size > 1) {
            ChainMessage(
                sequence = System.currentTimeMillis(),
                extraBody = extraBody,
                messages = arrayMsg.mapIndexed { index, msg ->
                    transformArrayMsg(isPrivateMsg, index.toLong(), msg)
                }
            )
        } else if (arrayMsg.size == 1) {
            transformArrayMsg(
                isPrivate = isPrivateMsg,
                index = System.currentTimeMillis(),
                arrayMsg = arrayMsg[0],
                extraBody = extraBody
            )
        } else {
            throw IllegalArgumentException("Message to be transformed is empty")
        }
    }

    private fun transformArrayMsg(isPrivate: Boolean, index: Long, arrayMsg: ArrayMsg, extraBody: ExtraBody? = null): AbstractMessage {
        return when (val message = NapCatMessageUtils.fromArrayMsg(arrayMsg)) {
            is NapCatTextMessage -> TextMessage(
                sequence = index,
                extraBody = extraBody,
                message = message.text
            )

            is NapCatFaceMessage -> TextMessage(
                sequence = index,
                extraBody = extraBody,
                message = message.raw.faceText
            )

            is NapCatReplyMessage -> {
                val quotedMessage = if (isPrivate) {
                    napCatPrivateMessageRepository
                        .findByMessageId(message.id.toInt())
                        .getOrNull(0)
                        ?.message
                } else {
                    napCatGroupMessageRepository
                        .findByMessageId(message.id.toInt())
                        .getOrNull(0)
                        ?.message
                }

                QuoteMessage(
                    sequence = index,
                    extraBody = extraBody,
                    message = quotedMessage?.let {
                        transformArrayMsgList(isPrivate, ShiroUtils.rawToArrayMsg(it), extraBody)
                    } ?: TextMessage(sequence = 0, message = "", extraBody = null)
                )
            }

            else -> throw IllegalArgumentException("Unsupported message type ${message.messageType}")
        }
    }
}