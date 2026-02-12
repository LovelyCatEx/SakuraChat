/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.core

import com.lovelycatv.sakurachat.entity.aggregated.AggregatedAgentEntity
import com.lovelycatv.sakurachat.service.AgentContextService
import com.lovelycatv.sakurachat.service.IMChannelMessageService
import com.lovelycatv.sakurachat.service.UserPointsService
import org.springframework.stereotype.Component

@Component
class SakuraChatAgentInstanceManager(
    private val agentContextService: AgentContextService,
    private val imChannelMessageService: IMChannelMessageService,
    private val userPointsService: UserPointsService
) {
    private val instances = mutableMapOf<Long, SakuraChatAgent>()

    fun getAgent(agentId: Long): SakuraChatAgent? {
        return instances[agentId]
    }

    fun addAgent(agent: SakuraChatAgent): SakuraChatAgent {
        instances[agent.id] = agent
        return agent
    }

    fun addAgent(agent: AggregatedAgentEntity): SakuraChatAgent {
        return this.addAgent(
            SakuraChatAgent(
                agent = agent,
                userPointsService = userPointsService,
                agentContextService = agentContextService,
                imChannelMessageService = imChannelMessageService
            )
        )
    }
}