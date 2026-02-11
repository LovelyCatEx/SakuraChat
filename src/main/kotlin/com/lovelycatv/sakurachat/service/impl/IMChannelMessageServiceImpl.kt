/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.service.impl

import com.lovelycatv.sakurachat.core.AbstractSakuraChatChannelMember
import com.lovelycatv.sakurachat.core.SakuraChatMessageChannel
import com.lovelycatv.sakurachat.core.SakuraChatMessageExtra
import com.lovelycatv.sakurachat.core.im.message.AbstractMessage
import com.lovelycatv.sakurachat.core.im.message.ErrorMessage
import com.lovelycatv.sakurachat.entity.channel.IMChannelMessageEntity
import com.lovelycatv.sakurachat.repository.IMChannelMessageRepository
import com.lovelycatv.sakurachat.service.ChannelMessageSerializationService
import com.lovelycatv.sakurachat.service.IMChannelMessageService
import com.lovelycatv.sakurachat.utils.SnowIdGenerator
import com.lovelycatv.sakurachat.utils.toJSONString
import com.lovelycatv.vertex.log.logger
import org.springframework.stereotype.Service

@Service
class IMChannelMessageServiceImpl(
    private val imChannelMessageRepository: IMChannelMessageRepository,
    private val snowIdGenerator: SnowIdGenerator,
    private val channelMessageSerializationService: ChannelMessageSerializationService
) : IMChannelMessageService {
    private val logger = logger()

    override fun getRepository(): IMChannelMessageRepository {
        return this.imChannelMessageRepository
    }

    override fun saveMessage(
        channel: SakuraChatMessageChannel,
        sender: AbstractSakuraChatChannelMember,
        message: AbstractMessage
    ) {
        // Check message extra
        if (!SakuraChatMessageExtra.isCapable(message.extraBody)) {
            if (message.extraBody != null)  {
                throw IllegalArgumentException("${message.extraBody::class.java} cannot be cast to ${SakuraChatMessageExtra::class.qualifiedName}")
            } else {
                throw IllegalArgumentException("Null extra body cannot be cast to ${SakuraChatMessageExtra::class.qualifiedName}")
            }
        }

        // Check whether the message is ErrorMessage
        if (message is ErrorMessage) {
            logger.warn("An error message cannot be saved into database, message: ${message.toJSONString()}")
            return
        }

        this.getRepository().save(
            IMChannelMessageEntity(
                id = snowIdGenerator.nextId(),
                channelId = channel.channelId,
                memberType = sender.memberType.typeCode,
                memberId = sender.id,
                platform = message.extraBody.getPlatformType().platformId,
                content = channelMessageSerializationService.toJSONString(message),
                createdTime = System.currentTimeMillis(),
                modifiedTime = System.currentTimeMillis(),
                deletedTime = null
            )
        )
    }
}