/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.service

import com.lovelycatv.sakurachat.adapters.thirdparty.account.ThirdPartyAccountAdapter
import com.lovelycatv.sakurachat.entity.thirdparty.ThirdPartyAccountEntity
import com.lovelycatv.sakurachat.repository.ThirdPartyAccountRepository
import com.lovelycatv.sakurachat.types.ThirdPartyPlatform

interface ThirdPartyAccountService : BaseService<ThirdPartyAccountRepository, ThirdPartyAccountEntity, Long> {
    fun getAccountByPlatformAndAccountId(
        platform: ThirdPartyPlatform,
        accountId: String
    ): ThirdPartyAccountEntity?

    fun getAccountAdapter(platform: ThirdPartyPlatform, platformAccount: Class<out Any>): ThirdPartyAccountAdapter<Any>

    fun getOrAddAccount(platform: ThirdPartyPlatform, platformAccount: Any): ThirdPartyAccountEntity

    fun getAccountIdByPlatformAccountObject(platform: ThirdPartyPlatform, platformAccount: Any): String {
        return this.getAccountAdapter(platform, platformAccount::class.java).getAccountId(platformAccount)
    }
}