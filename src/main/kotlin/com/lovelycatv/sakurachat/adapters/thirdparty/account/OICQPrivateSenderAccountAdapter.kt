/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.adapters.thirdparty.account

import com.lovelycatv.sakurachat.types.ThirdPartyPlatform
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent
import org.springframework.stereotype.Component

@Component
class OICQPrivateSenderAccountAdapter : ThirdPartyAccountAdapter<PrivateMessageEvent.PrivateSender> {
    override fun getPlatform(): ThirdPartyPlatform {
        return ThirdPartyPlatform.NAPCAT_OICQ
    }

    override fun getThirdPartyAccountClass(): Class<PrivateMessageEvent.PrivateSender> {
        return PrivateMessageEvent.PrivateSender::class.java
    }

    override fun getAccountId(thirdPartyAccount: PrivateMessageEvent.PrivateSender): String {
        return thirdPartyAccount.userId.toString()
    }

    override fun getNickName(thirdPartyAccount: PrivateMessageEvent.PrivateSender): String {
        return thirdPartyAccount.nickname.toString()
    }
}