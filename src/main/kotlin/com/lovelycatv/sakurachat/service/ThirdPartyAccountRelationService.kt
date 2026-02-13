/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.service

import com.lovelycatv.sakurachat.repository.AgentThirdPartyAccountRelationRepository
import com.lovelycatv.sakurachat.repository.UserThirdPartyAccountRelationRepository
import org.springframework.transaction.annotation.Transactional

interface ThirdPartyAccountRelationService {
    fun getAgentThirdPartyAccountRelationRepository(): AgentThirdPartyAccountRelationRepository

    fun getUserThirdPartyAccountRelationRepository(): UserThirdPartyAccountRelationRepository

    @Transactional
    fun bindUserToThirdPartyAccount(userId: Long, thirdPartyAccountEntityId: Long)

    @Transactional
    fun unbindUserFromThirdPartyAccount(userId: Long, thirdPartyAccountEntityId: Long)

    @Transactional
    fun bindAgentToThirdPartyAccount(agentId: Long, thirdPartyAccountEntityId: Long)

    @Transactional
    fun unbindAgentToThirdPartyAccount(agentId: Long, thirdPartyAccountEntityId: Long)
}