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
import org.springframework.stereotype.Component

@Component
class LarkIMAccessor : IThirdPartyIMAccessor<LarkRestClient, String> {
    override fun getPlatform(): ThirdPartyPlatform {
        return ThirdPartyPlatform.LARK
    }

    override suspend fun sendPrivateMessage(
        invoker: LarkRestClient,
        target: String,
        message: AbstractMessage
    ): Boolean {
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

    override suspend fun resolveTargetByPlatformAccountId(platformAccountId: String): String? {
        return platformAccountId
    }
}