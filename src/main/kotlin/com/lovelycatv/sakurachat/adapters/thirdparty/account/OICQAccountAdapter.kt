/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.adapters.thirdparty.account

import com.lovelycatv.sakurachat.types.ThirdPartyPlatform
import com.mikuac.shiro.core.Bot
import org.springframework.stereotype.Component

@Component
class OICQAccountAdapter : ThirdPartyAccountAdapter<Bot> {
    override fun getPlatform(): ThirdPartyPlatform {
        return ThirdPartyPlatform.NAPCAT_OICQ
    }

    override fun getThirdPartyAccountClass(): Class<Bot> {
        return Bot::class.java
    }

    override fun getAccountId(thirdPartyAccount: Bot): String {
        return thirdPartyAccount.selfId.toString()
    }

    override fun getNickName(thirdPartyAccount: Bot): String {
        return thirdPartyAccount.selfId.toString()
    }
}