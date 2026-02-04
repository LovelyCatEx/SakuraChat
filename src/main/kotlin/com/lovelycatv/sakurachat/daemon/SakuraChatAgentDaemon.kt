/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.daemon

import com.lovelycatv.sakurachat.repository.AgentChannelRelationRepository
import com.lovelycatv.sakurachat.repository.AgentRepository
import com.lovelycatv.vertex.log.logger
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class SakuraChatAgentDaemon(
    private val agentRepository: AgentRepository,
    private val agentChannelRelationRepository: AgentChannelRelationRepository
) {
    private val logger = logger()

    @Scheduled(cron = "0 */5 * * * *")
    fun check() {
        logger.info("Checking agents <=> channels")

        val allAgents = agentRepository.findAll()
    }
}