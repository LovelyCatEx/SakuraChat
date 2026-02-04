/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.adapters

import com.lovelycatv.sakurachat.entity.ThirdPartyAccountEntity
import com.lovelycatv.sakurachat.types.ThirdPartyPlatform
import com.mikuac.shiro.core.Bot
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent
import org.springframework.stereotype.Component

@Component
class OICQPrivateSenderAccountAdapter : ThirdPartyAccountAdapter<PrivateMessageEvent.PrivateSender> {
    override fun getPlatform(): ThirdPartyPlatform {
        return ThirdPartyPlatform.OICQ
    }

    override fun getThirdPartyAccountClass(): Class<PrivateMessageEvent.PrivateSender> {
        return PrivateMessageEvent.PrivateSender::class.java
    }

    override fun transform(thirdPartyAccount: PrivateMessageEvent.PrivateSender): ThirdPartyAccountEntity {
        return ThirdPartyAccountEntity(
            accountId = thirdPartyAccount.userId.toString(),
            nickname = thirdPartyAccount.nickname.toString(),
        )
    }
}