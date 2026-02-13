/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.service

import com.lovelycatv.sakurachat.entity.thirdparty.UserThirdPartyAccountRelationEntity
import com.lovelycatv.sakurachat.repository.UserThirdPartyAccountRelationRepository
import org.springframework.transaction.annotation.Transactional

interface UserThirdPartyAccountBindService : BaseService<
        UserThirdPartyAccountRelationRepository,
        UserThirdPartyAccountRelationEntity,
        UserThirdPartyAccountRelationEntity.PrimaryKey
> {
    fun generateUserBindCode(thirdPartyAccountEntityId: Long): String

    @Transactional
    fun bind(userId: Long, code: String)

    @Transactional
    fun unbind(userId: Long, thirdPartyAccountEntityId: Long)
}