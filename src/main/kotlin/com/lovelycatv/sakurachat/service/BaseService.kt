/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.service

import com.lovelycatv.sakurachat.exception.BusinessException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface BaseService<R: JpaRepository<T, ID>, T, ID: Any> {
    fun getRepository(): R

    suspend fun listByPage(page: Int, size: Int): Page<T> {
        return this.getRepository().findAll(
            Pageable
                .ofSize(size)
                .withPage(page - 1)
        )
    }

    suspend fun getById(id: ID): T? {
        return withContext(Dispatchers.IO) {
            getRepository().findById(id)
        }.orElse(null)
    }

    suspend fun getByIdOrThrow(
        id: ID,
        t: Throwable = BusinessException("Resource with id $id not found")
    ): T {
        return this.getById(id) ?: throw t
    }
}