/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.adapters.thirdparty.im

import com.lovelycatv.lark.LarkRestClient
import com.lovelycatv.lark.message.LarkTextMessage
import com.lovelycatv.lark.type.LarkIdType
import com.lovelycatv.sakurachat.core.im.message.AbstractMessage
import com.lovelycatv.sakurachat.core.im.message.TextMessage
import com.lovelycatv.sakurachat.core.im.thirdparty.IThirdPartyIMAccessor
import com.lovelycatv.sakurachat.types.ThirdPartyPlatform
import com.lovelycatv.sakurachat.utils.toJSONString
import com.lovelycatv.vertex.log.logger
import org.springframework.stereotype.Component

@Component
class LarkIMAccessor : IThirdPartyIMAccessor<LarkRestClient, String, String> {
    private val logger = logger()

    override fun getPlatform(): ThirdPartyPlatform {
        return ThirdPartyPlatform.LARK
    }

    override suspend fun sendPrivateMessage(
        invoker: LarkRestClient,
        target: String,
        message: AbstractMessage
    ): Boolean {
        if (message.isEmpty()) {
            logger.warn("sendPrivateMessage skipped due to empty message")
            return false
        }

        if (message is TextMessage) {
            invoker.sendMessage(
                LarkIdType.UNION_ID,
                target,
                LarkTextMessage(message.message)
            )
        } else {
            invoker.sendMessage(
                LarkIdType.UNION_ID,
                target,
                message.toJSONString()
            )
        }

        return true
    }

    override suspend fun sendGroupMessage(
        invoker: LarkRestClient,
        targetGroup: String,
        replyTarget: String,
        message: AbstractMessage
    ): Boolean {
        if (message.isEmpty()) {
            logger.warn("sendGroupMessage skipped due to empty message")
            return false
        }

        if (message is TextMessage) {
            invoker.sendMessage(
                LarkIdType.CHAT_ID,
                targetGroup,
                message.message
            )
        } else {
            invoker.sendMessage(
                LarkIdType.CHAT_ID,
                targetGroup,
                message.toJSONString()
            )
        }

        return true
    }

    override suspend fun resolveTargetByPlatformAccountId(platformAccountId: String): String? {
        return platformAccountId
    }

    override suspend fun resolveGroupTargetByPlatformGroupId(platformGroupId: String): String? {
        return platformGroupId
    }
}