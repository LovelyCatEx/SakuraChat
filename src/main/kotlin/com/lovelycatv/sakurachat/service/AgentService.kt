/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.service

import com.lovelycatv.sakurachat.controller.manager.dto.ManagerCreateAgentDTO
import com.lovelycatv.sakurachat.controller.manager.dto.ManagerUpdateAgentDTO
import com.lovelycatv.sakurachat.entity.AgentEntity
import com.lovelycatv.sakurachat.entity.aggregated.AggregatedAgentEntity
import com.lovelycatv.sakurachat.entity.thirdparty.ThirdPartyAccountEntity
import com.lovelycatv.sakurachat.repository.AgentRepository
import com.lovelycatv.sakurachat.request.PaginatedResponseData
import com.lovelycatv.sakurachat.types.ThirdPartyPlatform

interface AgentService : BaseService<AgentRepository, AgentEntity, Long> {
    fun getAgentByThirdPartyAccount(platform: ThirdPartyPlatform, accountId: String): AgentEntity?

    fun getRelatedThirdPartyAccounts(agentId: Long): Map<ThirdPartyPlatform, List<ThirdPartyAccountEntity>>

    fun toAggregatedAgentEntity(agent: AgentEntity): AggregatedAgentEntity

    suspend fun updateAgent(managerUpdateAgentDTO: ManagerUpdateAgentDTO)

    suspend fun createAgent(managerCreateAgentDTO: ManagerCreateAgentDTO)

    suspend fun search(keyword: String, page: Int, pageSize: Int): PaginatedResponseData<AgentEntity>
}