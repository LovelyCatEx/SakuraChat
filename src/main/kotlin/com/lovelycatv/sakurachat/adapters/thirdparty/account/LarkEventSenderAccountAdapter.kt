/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.adapters.thirdparty.account

import com.lark.oapi.service.im.v1.model.EventSender
import com.lovelycatv.sakurachat.types.ThirdPartyPlatform
import org.springframework.stereotype.Component

@Component
class LarkEventSenderAccountAdapter : ThirdPartyAccountAdapter<EventSender> {
    override fun getPlatform(): ThirdPartyPlatform {
        return ThirdPartyPlatform.LARK
    }

    override fun getThirdPartyAccountClass(): Class<EventSender> {
        return EventSender::class.java
    }

    override fun getAccountId(thirdPartyAccount: EventSender): String {
        return thirdPartyAccount.senderId.unionId
    }

    override fun getNickName(thirdPartyAccount: EventSender): String {
        return thirdPartyAccount.senderId.unionId
    }
}