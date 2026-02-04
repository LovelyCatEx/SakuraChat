/*
 * Copyright 2025 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.repository

import com.lovelycatv.sakurachat.entity.napcat.NapCatGroupMessageEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface NapCatGroupMessageRepository : JpaRepository<NapCatGroupMessageEntity, Long> {
    fun getByHash(hash: String): NapCatGroupMessageEntity?
}