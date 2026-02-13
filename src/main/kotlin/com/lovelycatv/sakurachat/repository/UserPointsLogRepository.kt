/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.repository

import com.lovelycatv.sakurachat.entity.UserPointsLogEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface UserPointsLogRepository : JpaRepository<UserPointsLogEntity, Long> {
    fun findAllByUserId(userId: Long, pageable: Pageable): Page<UserPointsLogEntity>

    @Query("SELECT SUM(ABS(l.deltaPoints)) FROM UserPointsLogEntity l WHERE l.deltaPoints < 0")
    fun getTotalPointsConsumed(): Long?
}