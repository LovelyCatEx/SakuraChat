/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.service

import com.lovelycatv.sakurachat.entity.UserPointsLogEntity
import com.lovelycatv.sakurachat.repository.UserPointsLogRepository
import com.lovelycatv.sakurachat.request.PageQuery
import com.lovelycatv.sakurachat.request.PaginatedResponseData
import com.lovelycatv.sakurachat.service.request.UserPointsConsumeRequest
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

interface UserPointsLogService : BaseService<UserPointsLogRepository, UserPointsLogEntity, Long> {
    @Transactional(propagation = Propagation.REQUIRED)
    fun record(userId: Long, request: UserPointsConsumeRequest): UserPointsLogEntity

    fun listUserPointsLogs(userId: Long, query: PageQuery): PaginatedResponseData<UserPointsLogEntity>
}