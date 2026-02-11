/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.utils

import com.lovelycatv.sakurachat.request.PaginatedResponseData
import org.springframework.data.domain.Page

class JpaExtensions private constructor()

fun <T> Page<T>.toPaginatedResponseData(): PaginatedResponseData<T> {
    return PaginatedResponseData(
        page = this.pageable.pageNumber,
        pageSize = this.pageable.pageSize,
        total = this.totalElements,
        totalPages = this.totalPages,
        records = this.toList()
    )
}