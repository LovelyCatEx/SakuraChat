/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.repository

import com.lovelycatv.sakurachat.entity.thirdparty.ThirdPartyAccountEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ThirdPartyAccountRepository : JpaRepository<ThirdPartyAccountEntity, Long> {
    fun getByAccountIdAndPlatform(accountId: String, platformId: Int): ThirdPartyAccountEntity?

    fun findAllByNicknameContainingIgnoreCaseOrAccountIdContainingIgnoreCase(
        nickname: String,
        accountId: String,
        pageable: Pageable
    ): Page<ThirdPartyAccountEntity>
}