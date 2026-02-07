/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.service

import com.lovelycatv.sakurachat.entity.AgentEntity
import com.lovelycatv.sakurachat.entity.aggregated.AggregatedAgentEntity
import com.lovelycatv.sakurachat.entity.thirdparty.ThirdPartyAccountEntity
import com.lovelycatv.sakurachat.repository.AgentRepository
import com.lovelycatv.sakurachat.types.ThirdPartyPlatform

interface AgentService : BaseService<AgentRepository> {
    fun getAgentByThirdPartyAccount(platform: ThirdPartyPlatform, accountId: String): AgentEntity?

    fun getRelatedThirdPartyAccounts(agentId: Long): Map<ThirdPartyPlatform, List<ThirdPartyAccountEntity>>

    fun toAggregatedAgentEntity(agent: AgentEntity): AggregatedAgentEntity
}