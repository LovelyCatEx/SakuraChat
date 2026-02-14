/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.service.impl

import com.lovelycatv.sakurachat.controller.manager.dto.ManagerUpdatePointsCdKeyDTO
import com.lovelycatv.sakurachat.entity.PointsCdKeyEntity
import com.lovelycatv.sakurachat.exception.BusinessException
import com.lovelycatv.sakurachat.repository.PointsCdKeyRepository
import com.lovelycatv.sakurachat.request.PageQuery
import com.lovelycatv.sakurachat.request.PaginatedResponseData
import com.lovelycatv.sakurachat.service.PointsCdKeyService
import com.lovelycatv.sakurachat.service.UserPointsService
import com.lovelycatv.sakurachat.service.request.UserPointsConsumeRequest
import com.lovelycatv.sakurachat.types.DatabaseTableType
import com.lovelycatv.sakurachat.types.PointsChangesReason
import com.lovelycatv.sakurachat.utils.SnowIdGenerator
import com.lovelycatv.sakurachat.utils.toPageable
import com.lovelycatv.sakurachat.utils.toPaginatedResponseData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PointsCdKeyServiceImpl(
    private val pointsCdKeyRepository: PointsCdKeyRepository,
    private val snowIdGenerator: SnowIdGenerator,
    private val userPointsService: UserPointsService
) : PointsCdKeyService {
    override fun getRepository(): PointsCdKeyRepository {
        return this.pointsCdKeyRepository
    }

    override suspend fun createCdKey(cdKey: String, points: Long, generatedBy: Long): PointsCdKeyEntity {
        val entity = PointsCdKeyEntity(
            id = snowIdGenerator.nextId(),
            cdKey = cdKey,
            points = points,
            generatedBy = generatedBy
        )
        return withContext(Dispatchers.IO) {
            getRepository().save(entity)
        }
    }

    override suspend fun updateCdKey(managerUpdatePointsCdKeyDTO: ManagerUpdatePointsCdKeyDTO): PointsCdKeyEntity {
        val existing = this.getByIdOrThrow(managerUpdatePointsCdKeyDTO.id)

        if (managerUpdatePointsCdKeyDTO.cdKey != null) {
            existing.cdKey = managerUpdatePointsCdKeyDTO.cdKey
        }

        if (managerUpdatePointsCdKeyDTO.points != null) {
            existing.points = managerUpdatePointsCdKeyDTO.points
        }

        if (managerUpdatePointsCdKeyDTO.generatedBy != null) {
            existing.generatedBy = managerUpdatePointsCdKeyDTO.generatedBy
        }

        if (managerUpdatePointsCdKeyDTO.usedBy != null) {
            existing.usedBy = managerUpdatePointsCdKeyDTO.usedBy
        }

        existing.modifiedTime = System.currentTimeMillis()

        return withContext(Dispatchers.IO) {
            getRepository().save(existing)
        }
    }

    override suspend fun search(keyword: String, pageQuery: PageQuery): PaginatedResponseData<PointsCdKeyEntity> {
        return withContext(Dispatchers.IO) {
            val result = getRepository().findAll(
                pageQuery.toPageable(Sort.Direction.DESC, "createdTime")
            )
            result.toPaginatedResponseData()
        }
    }

    override suspend fun findByCdKey(cdKey: String): PointsCdKeyEntity? {
        return withContext(Dispatchers.IO) {
            getRepository().findByCdKey(cdKey)
        }
    }

    @Transactional
    override suspend fun redeemCdKey(userId: Long, cdKey: String): Long {
        val cdKeyEntity = findByCdKey(cdKey)
            ?: throw BusinessException("invalid cdkey")

        if (cdKeyEntity.usedBy != null) {
            throw BusinessException("this cdkey has already been redeemed.")
        }

        userPointsService.addPoints(
            userId = userId,
            points = cdKeyEntity.points,
            reason = PointsChangesReason.CDKEY_REDEEM,
            reasonType = PointsChangesReason.CDKEY_REDEEM.reasonId,
            associations = listOf(
                UserPointsConsumeRequest.Association(
                    tableType = DatabaseTableType.POINTS_CD_KEYS,
                    id = cdKeyEntity.id
                )
            )
        )

        cdKeyEntity.usedBy = userId
        cdKeyEntity.modifiedTime = System.currentTimeMillis()
        getRepository().save(cdKeyEntity)

        return cdKeyEntity.points
    }
}