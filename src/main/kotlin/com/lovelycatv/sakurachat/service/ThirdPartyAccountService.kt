/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.service

import com.lovelycatv.sakurachat.entity.ThirdPartyAccountEntity
import com.lovelycatv.sakurachat.repository.ThirdPartyAccountRepository
import com.lovelycatv.sakurachat.types.ThirdPartyPlatform

interface ThirdPartyAccountService : BaseService<ThirdPartyAccountRepository> {
    fun getAccountByPlatformAndAccountId(
        platform: ThirdPartyPlatform,
        accountId: String
    ): ThirdPartyAccountEntity?

    fun getOrAddAccount(platform: ThirdPartyPlatform, platformAccount: Any): ThirdPartyAccountEntity
}