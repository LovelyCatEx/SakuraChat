/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.repository

import com.lovelycatv.sakurachat.entity.channel.IMChannelEntity
import org.springframework.data.jpa.repository.JpaRepository

interface IMChannelRepository : JpaRepository<IMChannelEntity, Long> {
    fun findByChannelIdentifier(channelIdentifier: String): MutableList<IMChannelEntity>
}