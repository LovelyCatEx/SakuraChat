/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.repository

import com.lovelycatv.sakurachat.entity.thirdparty.AgentThirdPartyAccountRelationEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface AgentThirdPartyAccountRelationRepository : JpaRepository<AgentThirdPartyAccountRelationEntity, AgentThirdPartyAccountRelationEntity.PrimaryKey> {
    @Query("SELECT r FROM AgentThirdPartyAccountRelationEntity r WHERE r.primaryKey.agentId = :agentId")
    fun findByAgentId(@Param("agentId") agentId: Long): List<AgentThirdPartyAccountRelationEntity>

    @Query("SELECT r FROM AgentThirdPartyAccountRelationEntity r WHERE r.primaryKey.thirdPartyAccountId = :thirdPartyAccountId")
    fun findByThirdPartyAccountId(@Param("thirdPartyAccountId") thirdPartyAccountId: Long?): List<AgentThirdPartyAccountRelationEntity>
}