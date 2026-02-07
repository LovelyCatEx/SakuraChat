/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.adapters.thirdparty.account

import com.lovelycatv.sakurachat.entity.thirdparty.ThirdPartyAccountEntity
import com.lovelycatv.sakurachat.types.ThirdPartyPlatform
import com.mikuac.shiro.dto.event.message.GroupMessageEvent
import org.springframework.stereotype.Component

@Component
class OICQGroupSenderAccountAdapter : ThirdPartyAccountAdapter<GroupMessageEvent.GroupSender> {
    override fun getPlatform(): ThirdPartyPlatform {
        return ThirdPartyPlatform.OICQ
    }

    override fun getThirdPartyAccountClass(): Class<GroupMessageEvent.GroupSender> {
        return GroupMessageEvent.GroupSender::class.java
    }

    override fun transform(thirdPartyAccount: GroupMessageEvent.GroupSender): ThirdPartyAccountEntity {
        return ThirdPartyAccountEntity(
            accountId = thirdPartyAccount.userId.toString(),
            nickname = thirdPartyAccount.nickname.toString(),
        )
    }
}