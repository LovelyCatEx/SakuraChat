/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.adapters.thirdparty.im

import com.lovelycatv.sakurachat.core.im.message.AbstractMessage
import com.lovelycatv.sakurachat.core.im.message.TextMessage
import com.lovelycatv.sakurachat.core.im.thirdparty.IThirdPartyIMAccessor
import com.lovelycatv.sakurachat.types.ThirdPartyPlatform
import com.lovelycatv.sakurachat.utils.toJSONString
import com.mikuac.shiro.common.utils.MsgUtils
import com.mikuac.shiro.core.Bot
import org.springframework.stereotype.Component

@Component
class OICQIMAccessor : IThirdPartyIMAccessor<Bot, Long, Long> {
    override fun getPlatform(): ThirdPartyPlatform {
        return ThirdPartyPlatform.NAPCAT_OICQ
    }

    override suspend fun sendPrivateMessage(
        invoker: Bot,
        target: Long,
        message: AbstractMessage
    ): Boolean {
        val result = if (message is TextMessage) {
            invoker.sendPrivateMsg(target, message.message, true)
        } else {
            invoker.sendPrivateMsg(target, message.toJSONString(), true)
        }

        return result?.retCode == 0
    }

    override suspend fun sendGroupMessage(
        invoker: Bot,
        targetGroup: Long,
        replyTarget: Long,
        message: AbstractMessage
    ): Boolean {
        val result = if (message is TextMessage) {
            invoker.sendGroupMsg(
                targetGroup,
                MsgUtils
                    .builder()
                    .at(replyTarget)
                    .text(" ")
                    .text(message.message)
                    .build(),
                false
            )
        } else {
            invoker.sendGroupMsg(
                targetGroup,
                MsgUtils
                    .builder()
                    .at(replyTarget)
                    .text(" ")
                    .text(message.toJSONString())
                    .build(),
                false
            )
        }

        return result?.retCode == 0
    }

    override suspend fun resolveTargetByPlatformAccountId(platformAccountId: String): Long? {
        return platformAccountId.toLong()
    }

    override suspend fun resolveGroupTargetByPlatformGroupId(platformGroupId: String): Long? {
        return platformGroupId.toLong()
    }
}