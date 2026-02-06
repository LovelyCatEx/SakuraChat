/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.repository

import com.lovelycatv.sakurachat.entity.channel.UserChannelRelationEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface UserChannelRelationRepository : JpaRepository<UserChannelRelationEntity, UserChannelRelationEntity.PrimaryKey> {
    @Query("SELECT r FROM UserChannelRelationEntity r WHERE r.primaryKey.userId = :userId")
    fun findByUserId(@Param("userId") userId: Long): List<UserChannelRelationEntity>

    @Query("SELECT r FROM UserChannelRelationEntity r WHERE r.primaryKey.channelId = :channelId")
    fun findByChannelId(@Param("channelId") channelId: Long): List<UserChannelRelationEntity>
}