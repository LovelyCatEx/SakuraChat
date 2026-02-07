/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.adapters.thirdparty.account

import com.lovelycatv.lark.LarkRestClient
import com.lovelycatv.sakurachat.types.ThirdPartyPlatform
import org.springframework.stereotype.Component

@Component
class LarkRestClientAccountAdapter : ThirdPartyAccountAdapter<LarkRestClient> {
    override fun getPlatform(): ThirdPartyPlatform {
        return ThirdPartyPlatform.LARK
    }

    override fun getThirdPartyAccountClass(): Class<LarkRestClient> {
        return LarkRestClient::class.java
    }

    override fun getAccountId(thirdPartyAccount: LarkRestClient): String {
        return thirdPartyAccount.appId
    }

    override fun getNickName(thirdPartyAccount: LarkRestClient): String {
        return thirdPartyAccount.appId
    }
}