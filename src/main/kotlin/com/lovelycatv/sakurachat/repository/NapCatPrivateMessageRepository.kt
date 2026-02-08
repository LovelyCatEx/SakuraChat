/*
 * Copyright 2025 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.repository

import com.lovelycatv.sakurachat.entity.napcat.NapCatPrivateMessageEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface NapCatPrivateMessageRepository : JpaRepository<NapCatPrivateMessageEntity, Long> {
    fun findByMessageId(messageId: Int): MutableList<NapCatPrivateMessageEntity>
}