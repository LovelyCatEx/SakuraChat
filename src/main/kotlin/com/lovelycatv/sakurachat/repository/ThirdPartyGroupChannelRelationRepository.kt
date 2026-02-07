/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.repository

import com.lovelycatv.sakurachat.entity.thirdparty.ThirdPartyGroupChannelRelationEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ThirdPartyGroupChannelRelationRepository : JpaRepository<ThirdPartyGroupChannelRelationEntity, ThirdPartyGroupChannelRelationEntity.PrimaryKey> {
    @Query("SELECT r FROM ThirdPartyGroupChannelRelationEntity r WHERE r.primaryKey.thirdPartyGroupId = :thirdPartyGroupId")
    fun findByThirdPartyGroupId(@Param("thirdPartyGroupId") thirdPartyGroupId: Long): List<ThirdPartyGroupChannelRelationEntity>

    @Query("SELECT r FROM ThirdPartyGroupChannelRelationEntity r WHERE r.primaryKey.channelId = :channelId")
    fun findByChannelId(@Param("channelId") channelId: Long): List<ThirdPartyGroupChannelRelationEntity>
}