/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.service.impl

import com.lovelycatv.sakurachat.entity.UserPointsLogEntity
import com.lovelycatv.sakurachat.repository.UserPointsLogRepository
import com.lovelycatv.sakurachat.request.PageQuery
import com.lovelycatv.sakurachat.request.PaginatedResponseData
import com.lovelycatv.sakurachat.service.UserPointsLogService
import com.lovelycatv.sakurachat.service.request.UserPointsConsumeRequest
import com.lovelycatv.sakurachat.utils.SnowIdGenerator
import com.lovelycatv.sakurachat.utils.toPageable
import com.lovelycatv.sakurachat.utils.toPaginatedResponseData
import com.lovelycatv.vertex.log.logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
class UserPointsLogServiceImpl(
    private val userPointsLogRepository: UserPointsLogRepository,
    private val snowIdGenerator: SnowIdGenerator
) : UserPointsLogService {
    private val logger = logger()

    override fun getRepository(): UserPointsLogRepository {
        return this.userPointsLogRepository
    }

    @Transactional(propagation = Propagation.REQUIRED)
    override fun record(
        userId: Long,
        request: UserPointsConsumeRequest
    ): UserPointsLogEntity {
        val entity = UserPointsLogEntity(
            id = snowIdGenerator.nextId(),
            userId = userId,
            deltaPoints = request.delta,
            reasonType = request.reason.reasonId
        )

        request.associations.forEachIndexed { index, association ->
            when (index) {
                0 -> {
                    entity.relatedTableType1 = association.tableType.typeId
                    entity.relatedTableId1 = association.id
                }

                1 -> {
                    entity.relatedTableType2 = association.tableType.typeId
                    entity.relatedTableId2 = association.id
                }

                2 -> {
                    entity.relatedTableType3 = association.tableType.typeId
                    entity.relatedTableId3 = association.id
                }

                3 -> {
                    entity.relatedTableType4 = association.tableType.typeId
                    entity.relatedTableId4 = association.id
                }

                else -> {
                    throw IndexOutOfBoundsException("Association index $index out of bounds, maximum is 3")
                }
            }
        }

        return getRepository().save(entity).also {
            logger.info("User points log saved, user: $userId, request: $request")
        }
    }

    override fun listUserPointsLogs(
        userId: Long,
        query: PageQuery
    ): PaginatedResponseData<UserPointsLogEntity> {
        return getRepository().findAllByUserId(
            userId,
            query.toPageable(
                Sort.Direction.DESC,
                "createdTime",
            ),
        ).toPaginatedResponseData()
    }
}