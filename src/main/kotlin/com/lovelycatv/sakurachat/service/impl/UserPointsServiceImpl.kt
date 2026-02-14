/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.service.impl

import com.lovelycatv.sakurachat.entity.UserEntity
import com.lovelycatv.sakurachat.exception.BusinessException
import com.lovelycatv.sakurachat.repository.UserRepository
import com.lovelycatv.sakurachat.service.UserPointsLogService
import com.lovelycatv.sakurachat.service.UserPointsService
import com.lovelycatv.sakurachat.service.request.UserPointsConsumeRequest
import com.lovelycatv.sakurachat.types.PointsChangesReason
import com.lovelycatv.vertex.log.logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import kotlin.jvm.optionals.getOrNull

@Service
class UserPointsServiceImpl(
    private val userRepository: UserRepository,
    private val userPointsLogService: UserPointsLogService
) : UserPointsService {
    private val logger = logger()

    override fun getRepository(): UserRepository {
        return this.userRepository
    }

    override suspend fun hasPoints(userId: Long, minimum: Long): Boolean {
        val userPoints = withContext(Dispatchers.IO) {
            getRepository().getUserPoints(userId)
        } ?: return false

        return userPoints >= minimum
    }

    @Transactional(propagation = Propagation.REQUIRED)
    override fun consumePoints(userId: Long, request: UserPointsConsumeRequest): UserEntity {
        val user = getRepository().findById(userId).getOrNull()
            ?: throw BusinessException("User $userId not found")

        val consumesOrGains = request.consumedPoints >= 0

        val pointsAfterConsumed = user.points - request.consumedPoints

        if (pointsAfterConsumed < 0) {
            throw BusinessException("Insufficient points to consume, expect ${request.consumedPoints} points but ${user.points} last")
        }

        userPointsLogService.record(userId, request)

        return getRepository().save(
            user.apply {
                this.points = pointsAfterConsumed
                this.modifiedTime = System.currentTimeMillis()
            }
        ).also {
            logger.info("${if (consumesOrGains) "[-]" else "[+]"} User ${user.id} ${if (consumesOrGains) "consumed" else "gained"} ${request.consumedPoints} points")
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    override suspend fun addPoints(userId: Long, points: Long, reason: PointsChangesReason, reasonType: Int, associations: List<UserPointsConsumeRequest.Association> /* = java.util.List<com.lovelycatv.sakurachat.service.request.UserPointsConsumeRequest.Association> */) {
        val user = withContext(Dispatchers.IO) {
            getRepository().findById(userId).getOrNull()
        } ?: throw BusinessException("User $userId not found")

        val pointsAfterAdd = user.points + points

        val request = UserPointsConsumeRequest(
            reason = reason,
            consumedPoints = -points, // 负数表示添加积分
            afterBalance = pointsAfterAdd,
            associations = associations
        )

        withContext(Dispatchers.IO) {
            userPointsLogService.record(userId, request)

            getRepository().save(
                user.apply {
                    this.points = pointsAfterAdd
                    this.modifiedTime = System.currentTimeMillis()
                }
            ).also {
                logger.info("[+] User ${user.id} gained $points points for reason: ${reason.name}")
            }
        }
    }


}
