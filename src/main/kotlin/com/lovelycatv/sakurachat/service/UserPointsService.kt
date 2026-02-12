/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.service

import com.lovelycatv.sakurachat.entity.UserEntity
import com.lovelycatv.sakurachat.repository.UserRepository
import com.lovelycatv.sakurachat.service.request.UserPointsConsumeRequest
import com.lovelycatv.sakurachat.types.DatabaseTableType
import com.lovelycatv.sakurachat.types.PointsChangesReason
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

interface UserPointsService : BaseService<UserRepository, UserEntity, Long> {
    suspend fun hasPoints(userId: Long, minimum: Long): Boolean

    @Transactional(propagation = Propagation.REQUIRED)
    fun consumePoints(userId: Long, request: UserPointsConsumeRequest): UserEntity

    fun buildAgentChatCompletionsRequest(
        userId: Long,
        agentId: Long,
        chatModelId: Long,
        points: Long
    ): UserPointsConsumeRequest {
        return UserPointsConsumeRequest(
            reason = PointsChangesReason.AGENT_CHAT_COMPLETION,
            delta = points,
            associations = listOf(
                UserPointsConsumeRequest.Association(
                    tableType = DatabaseTableType.AGENTS,
                    id = agentId,
                ),
                UserPointsConsumeRequest.Association(
                    tableType = DatabaseTableType.CHAT_MODELS,
                    id = chatModelId
                )
            )
        )
    }
}