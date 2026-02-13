/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.service

import com.lovelycatv.sakurachat.adapters.thirdparty.account.ThirdPartyAccountAdapter
import com.lovelycatv.sakurachat.controller.manager.dto.ManagerCreateThirdPartyAccountDTO
import com.lovelycatv.sakurachat.controller.manager.dto.ManagerUpdateThirdPartyAccountDTO
import com.lovelycatv.sakurachat.entity.thirdparty.ThirdPartyAccountEntity
import com.lovelycatv.sakurachat.repository.ThirdPartyAccountRepository
import com.lovelycatv.sakurachat.request.PaginatedResponseData
import com.lovelycatv.sakurachat.types.ThirdPartyPlatform

interface ThirdPartyAccountService : BaseService<ThirdPartyAccountRepository, ThirdPartyAccountEntity, Long> {
    suspend fun updateThirdPartyAccount(managerUpdateThirdPartyAccountDTO: ManagerUpdateThirdPartyAccountDTO)

    suspend fun createThirdPartyAccount(managerCreateThirdPartyAccountDTO: ManagerCreateThirdPartyAccountDTO)

    fun getAccountByPlatformAndAccountId(
        platform: ThirdPartyPlatform,
        accountId: String
    ): ThirdPartyAccountEntity?

    fun getAccountAdapter(platform: ThirdPartyPlatform, platformAccount: Class<out Any>): ThirdPartyAccountAdapter<Any>

    fun getOrAddAccount(platform: ThirdPartyPlatform, platformAccount: Any): ThirdPartyAccountEntity

    fun getAccountIdByPlatformAccountObject(platform: ThirdPartyPlatform, platformAccount: Any): String {
        return this.getAccountAdapter(platform, platformAccount::class.java).getAccountId(platformAccount)
    }

    fun search(keyword: String, page: Int, pageSize: Int): PaginatedResponseData<ThirdPartyAccountEntity>

    fun getUnboundAccountsForAgent(agentId: Long, page: Int, pageSize: Int): PaginatedResponseData<ThirdPartyAccountEntity>
}
