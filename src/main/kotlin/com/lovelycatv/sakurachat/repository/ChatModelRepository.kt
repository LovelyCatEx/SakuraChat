/*
 * Copyright 2025 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.repository

import com.lovelycatv.sakurachat.entity.ChatModelEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ChatModelRepository : JpaRepository<ChatModelEntity, Long> {
    fun findAllByNameLikeOrDescriptionLikeOrQualifiedNameLike(
        name: String?,
        description: String?,
        qualifiedName: String?,
        pageable: Pageable
    ): Page<ChatModelEntity>
}