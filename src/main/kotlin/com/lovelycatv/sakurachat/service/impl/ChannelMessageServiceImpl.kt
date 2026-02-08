/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.service.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.lovelycatv.sakurachat.core.ISakuraChatMessageChannelMember
import com.lovelycatv.sakurachat.core.SakuraChatMessageChannel
import com.lovelycatv.sakurachat.core.im.message.AbstractMessage
import com.lovelycatv.sakurachat.entity.channel.ChannelMessageEntity
import com.lovelycatv.sakurachat.repository.ChannelMessageRepository
import com.lovelycatv.sakurachat.service.ChannelMessageSerializationService
import com.lovelycatv.sakurachat.service.ChannelMessageService
import com.lovelycatv.sakurachat.utils.SnowIdGenerator
import org.springframework.stereotype.Service

@Service
class ChannelMessageServiceImpl(
    private val channelMessageRepository: ChannelMessageRepository,
    private val snowIdGenerator: SnowIdGenerator,
    private val objectMapper: ObjectMapper,
    private val channelMessageSerializationService: ChannelMessageSerializationService
) : ChannelMessageService {
    override fun getRepository(): ChannelMessageRepository {
        return this.channelMessageRepository
    }

    override fun saveMessage(
        channel: SakuraChatMessageChannel,
        sender: ISakuraChatMessageChannelMember,
        message: AbstractMessage
    ) {
        this.getRepository().save(
            ChannelMessageEntity(
                id = snowIdGenerator.nextId(),
                channelId = channel.channelId,
                memberId = sender.memberId,
                content = channelMessageSerializationService.toJSONString(message),
                createdTime = System.currentTimeMillis(),
                modifiedTime = System.currentTimeMillis(),
                deletedTime = null
            )
        )
    }
}