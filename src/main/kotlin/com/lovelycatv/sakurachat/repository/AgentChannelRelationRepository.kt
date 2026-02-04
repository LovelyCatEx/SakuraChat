/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.repository

import com.lovelycatv.sakurachat.entity.AgentChannelRelationEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface AgentChannelRelationRepository : JpaRepository<AgentChannelRelationEntity, AgentChannelRelationEntity.PrimaryKey> {
    @Query("SELECT r FROM AgentChannelRelationEntity r WHERE r.primaryKey.agentId = :agentId")
    fun findByAgentId(@Param("agentId") agentId: Long): List<AgentChannelRelationEntity>

    @Query("SELECT r FROM AgentChannelRelationEntity r WHERE r.primaryKey.channelId = :channelId")
    fun findByChannelId(@Param("channelId") channelId: Long): List<AgentChannelRelationEntity>
}