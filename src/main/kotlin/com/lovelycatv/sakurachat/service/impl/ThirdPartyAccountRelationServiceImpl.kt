/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.service.impl

import com.lovelycatv.sakurachat.entity.thirdparty.AgentThirdPartyAccountRelationEntity
import com.lovelycatv.sakurachat.entity.thirdparty.UserThirdPartyAccountRelationEntity
import com.lovelycatv.sakurachat.repository.AgentThirdPartyAccountRelationRepository
import com.lovelycatv.sakurachat.repository.UserThirdPartyAccountRelationRepository
import com.lovelycatv.sakurachat.service.ThirdPartyAccountRelationService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ThirdPartyAccountRelationServiceImpl(
    private val agentThirdPartyAccountRelationRepository: AgentThirdPartyAccountRelationRepository,
    private val userThirdPartyAccountRelationRepository: UserThirdPartyAccountRelationRepository
) : ThirdPartyAccountRelationService {
    override fun getAgentThirdPartyAccountRelationRepository(): AgentThirdPartyAccountRelationRepository {
        return this.agentThirdPartyAccountRelationRepository
    }

    @Transactional
    override fun getUserThirdPartyAccountRelationRepository(): UserThirdPartyAccountRelationRepository {
        return this.userThirdPartyAccountRelationRepository
    }

    @Transactional
    override fun bindUserToThirdPartyAccount(userId: Long, thirdPartyAccountEntityId: Long) {
        val alreadyExists = this.getUserThirdPartyAccountRelationRepository()
            .findByUserId(userId)
            .any { it.primaryKey.thirdPartyAccountId == thirdPartyAccountEntityId }

        if (!alreadyExists) {
            this.getUserThirdPartyAccountRelationRepository()
                .save(
                    UserThirdPartyAccountRelationEntity(
                        primaryKey = UserThirdPartyAccountRelationEntity.PrimaryKey(
                            userId = userId,
                            thirdPartyAccountId = thirdPartyAccountEntityId
                        )
                    )
                )
        }
    }

    @Transactional
    override fun unbindUserFromThirdPartyAccount(userId: Long, thirdPartyAccountEntityId: Long) {
        this.getUserThirdPartyAccountRelationRepository()
            .deleteByUserIdAndThirdPartyAccountId(userId, thirdPartyAccountEntityId)
    }

    @Transactional
    override fun bindAgentToThirdPartyAccount(agentId: Long, thirdPartyAccountEntityId: Long) {
        val alreadyExists = this.getAgentThirdPartyAccountRelationRepository()
            .findByAgentId(agentId)
            .any { it.primaryKey.thirdPartyAccountId == thirdPartyAccountEntityId }

        if (!alreadyExists) {
            this.getAgentThirdPartyAccountRelationRepository()
                .save(
                    AgentThirdPartyAccountRelationEntity(
                        primaryKey = AgentThirdPartyAccountRelationEntity.PrimaryKey(
                            agentId = agentId,
                            thirdPartyAccountId = thirdPartyAccountEntityId
                        )
                    )
                )
        }
    }

    override fun unbindAgentToThirdPartyAccount(agentId: Long, thirdPartyAccountEntityId: Long) {
        this.getAgentThirdPartyAccountRelationRepository()
            .deleteByAgentIdAndThirdPartyAccountId(agentId, thirdPartyAccountEntityId)
    }
}