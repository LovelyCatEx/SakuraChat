/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.repository

import com.lovelycatv.sakurachat.entity.thirdparty.UserThirdPartyAccountRelationEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface UserThirdPartyAccountRelationRepository : JpaRepository<UserThirdPartyAccountRelationEntity, UserThirdPartyAccountRelationEntity.PrimaryKey> {
    @Query("SELECT r FROM UserThirdPartyAccountRelationEntity r WHERE r.primaryKey.userId = :userId")
    fun findByAgentId(@Param("userId") userId: Long): List<UserThirdPartyAccountRelationEntity>

    @Query("SELECT r FROM UserThirdPartyAccountRelationEntity r WHERE r.primaryKey.thirdPartyAccountId = :thirdPartyAccountId")
    fun findByThirdPartyAccountId(@Param("thirdPartyAccountId") thirdPartyAccountId: Long): List<UserThirdPartyAccountRelationEntity>
}