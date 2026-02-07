/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.repository

import com.lovelycatv.sakurachat.entity.thirdparty.ThirdPartyGroupEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ThirdPartyGroupRepository : JpaRepository<ThirdPartyGroupEntity, Long> {
    fun findByPlatformAndGroupId(platformId: Int, groupId: String): ThirdPartyGroupEntity?
}