/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.service

import com.lovelycatv.sakurachat.entity.channel.IMChannelEntity
import com.lovelycatv.sakurachat.repository.IMChannelRepository
import org.springframework.transaction.annotation.Transactional

interface IMChannelService : BaseService<IMChannelRepository> {
    @Transactional
    suspend fun getOrCreatePrivateChannelByUserIdAndAgentId(
        userId: Long,
        agentId: Long
    ): IMChannelEntity

    @Transactional
    suspend fun getOrCreateGroupChannelByUserIdAndAgentId(
        groupEntityId: Long,
        userId: Long,
        agentId: Long
    ): IMChannelEntity
}