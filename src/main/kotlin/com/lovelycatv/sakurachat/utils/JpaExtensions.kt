/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.utils

import com.lovelycatv.sakurachat.request.PageQuery
import com.lovelycatv.sakurachat.request.PaginatedResponseData
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

class JpaExtensions private constructor()

fun <T> Page<T>.toPaginatedResponseData(): PaginatedResponseData<T> {
    return this.toPaginatedResponseData { it }
}

fun <T, R> Page<T>.toPaginatedResponseData(recordTransform: (T) -> R): PaginatedResponseData<R> {
    return PaginatedResponseData(
        page = this.pageable.pageNumber,
        pageSize = this.pageable.pageSize,
        total = this.totalElements,
        totalPages = this.totalPages,
        records = this.toList().map { recordTransform(it) }
    )
}

fun PageQuery.toPageable(sortDirection: Sort.Direction? = null, vararg sortColumns: String): PageRequest {
    return if (sortDirection == null || sortColumns.isEmpty()) {
        PageRequest
            .of(
                this.page - 1,
                this.pageSize
            )
    } else {
        PageRequest
            .of(
                this.page - 1,
                this.pageSize,
                sortDirection,
                *sortColumns
            )
    }
}