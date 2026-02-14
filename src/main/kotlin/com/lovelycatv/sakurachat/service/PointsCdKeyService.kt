/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.service

import com.lovelycatv.sakurachat.controller.manager.dto.ManagerUpdatePointsCdKeyDTO
import com.lovelycatv.sakurachat.entity.PointsCdKeyEntity
import com.lovelycatv.sakurachat.repository.PointsCdKeyRepository
import com.lovelycatv.sakurachat.request.PageQuery
import com.lovelycatv.sakurachat.request.PaginatedResponseData
import org.springframework.transaction.annotation.Transactional

interface PointsCdKeyService : BaseService<PointsCdKeyRepository, PointsCdKeyEntity, Long> {
    suspend fun createCdKey(cdKey: String, points: Long, generatedBy: Long): PointsCdKeyEntity
    suspend fun updateCdKey(managerUpdatePointsCdKeyDTO: ManagerUpdatePointsCdKeyDTO): PointsCdKeyEntity

    suspend fun search(keyword: String, pageQuery: PageQuery): PaginatedResponseData<PointsCdKeyEntity>

    suspend fun findByCdKey(cdKey: String): PointsCdKeyEntity?

    @Transactional
    suspend fun redeemCdKey(userId: Long, cdKey: String): Long
}