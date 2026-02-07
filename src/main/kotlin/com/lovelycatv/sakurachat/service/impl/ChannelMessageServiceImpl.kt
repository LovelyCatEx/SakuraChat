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
import com.lovelycatv.sakurachat.service.ChannelMessageService
import com.lovelycatv.sakurachat.utils.SnowIdGenerator
import org.springframework.stereotype.Service

@Service
class ChannelMessageServiceImpl(
    private val channelMessageRepository: ChannelMessageRepository,
    private val snowIdGenerator: SnowIdGenerator,
    private val objectMapper: ObjectMapper
) : ChannelMessageService {
    override fun getRepository(): ChannelMessageRepository {
        return this.channelMessageRepository
    }

    override fun saveMessage(
        channel: SakuraChatMessageChannel,
        sender: ISakuraChatMessageChannelMember,
        message: AbstractMessage
    ) {
        @Suppress("UNCHECKED_CAST")
        val messageMap = objectMapper.convertValue(message, Map::class.java) as MutableMap<String, Any>

        messageMap.remove("sequence")
        messageMap.remove("extraBody")

        this.getRepository().save(
            ChannelMessageEntity(
                id = snowIdGenerator.nextId(),
                channelId = channel.channelId,
                memberId = sender.memberId,
                content = objectMapper.writeValueAsString(messageMap),
                createdTime = System.currentTimeMillis(),
                modifiedTime = System.currentTimeMillis(),
                deletedTime = null
            )
        )
    }
}