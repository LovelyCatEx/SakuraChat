/*
 * Copyright 2025 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.repository

import com.lovelycatv.sakurachat.entity.UserRoleRelationEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface UserRoleRelationRepository : JpaRepository<UserRoleRelationEntity, Long> {
    fun findAllByUserId(userId: Long): List<UserRoleRelationEntity>

    fun findAllByUserIdIn(userIds: List<Long>): List<UserRoleRelationEntity>

    @Modifying
    @Query("DELETE FROM UserRoleRelationEntity r WHERE r.userId = :userId")
    fun deleteAllByUserId(userId: Long)
    fun deleteByUserIdAndRoleId(userId: Long, roleId: Long)
}