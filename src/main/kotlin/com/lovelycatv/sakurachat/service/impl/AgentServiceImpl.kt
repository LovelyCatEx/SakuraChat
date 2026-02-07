/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.service.impl

import com.lovelycatv.sakurachat.entity.AgentEntity
import com.lovelycatv.sakurachat.entity.aggregated.AggregatedAgentEntity
import com.lovelycatv.sakurachat.entity.thirdparty.ThirdPartyAccountEntity
import com.lovelycatv.sakurachat.repository.AgentRepository
import com.lovelycatv.sakurachat.repository.AgentThirdPartyAccountRelationRepository
import com.lovelycatv.sakurachat.service.AgentService
import com.lovelycatv.sakurachat.service.ChatModelService
import com.lovelycatv.sakurachat.service.ThirdPartyAccountService
import com.lovelycatv.sakurachat.types.ThirdPartyPlatform
import com.lovelycatv.vertex.log.logger
import org.springframework.stereotype.Service

@Service
class AgentServiceImpl(
    private val agentRepository: AgentRepository,
    private val thirdPartyAccountService: ThirdPartyAccountService,
    private val agentThirdPartyAccountRelationRepository: AgentThirdPartyAccountRelationRepository,
    private val chatModelService: ChatModelService
) : AgentService {
    private val logger = logger()

    override fun getRepository(): AgentRepository {
        return this.agentRepository
    }

    override fun getAgentByThirdPartyAccount(
        platform: ThirdPartyPlatform,
        accountId: String
    ): AgentEntity? {
        val thirdPartyAccount = this.thirdPartyAccountService
            .getAccountByPlatformAndAccountId(platform, accountId)
            ?: return null

        val agentId = this.agentThirdPartyAccountRelationRepository
            .findByThirdPartyAccountId(thirdPartyAccount.id)
            .also { relations ->
                if (relations.size > 1) {
                    logger.warn("Third party account $accountId of platform ${platform.name} has bind more than 1 agent, " +
                            "agentIds: ${relations.joinToString { it.primaryKey.agentId.toString() }}"
                    )
                }
            }
            .firstOrNull()
            ?.primaryKey?.agentId
            ?: return null

        return this.agentRepository.findById(agentId).orElse(null)
    }

    override fun getRelatedThirdPartyAccounts(agentId: Long): Map<ThirdPartyPlatform, List<ThirdPartyAccountEntity>> {
        val relatedThirdPartyAccountEntityIds = this.agentThirdPartyAccountRelationRepository
            .findByAgentId(agentId)
            .map { it.primaryKey.thirdPartyAccountId }

        val accounts = this.thirdPartyAccountService
            .getRepository()
            .findAllById(relatedThirdPartyAccountEntityIds)

        return accounts.groupBy { it.getPlatformType() }
    }

    override fun toAggregatedAgentEntity(agent: AgentEntity): AggregatedAgentEntity {
        return AggregatedAgentEntity(
            agent = agent,
            chatModel = chatModelService.getAggregatedChatModelEntityById(agent.chatModelId)
        )
    }
}