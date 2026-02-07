/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.core

import com.lovelycatv.sakurachat.entity.aggregated.AggregatedAgentEntity
import org.springframework.stereotype.Component

@Component
class SakuraChatAgentInstanceManager {
    private val instances = mutableMapOf<String, SakuraChatAgent>()

    fun getAgent(agentId: Long): SakuraChatAgent? {
        return instances[SakuraChatAgent.buildMemberId(agentId)]
    }

    fun addAgent(agent: SakuraChatAgent): SakuraChatAgent {
        instances[agent.memberId] = agent
        return agent
    }

    fun addAgent(agent: AggregatedAgentEntity): SakuraChatAgent {
        return this.addAgent(
            SakuraChatAgent(
                agent = agent
            )
        )
    }
}