/*
 * Copyright 2025 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.napcat

import com.lovelycatv.sakurachat.entity.napcat.NapCatGroupMessageEntity
import com.lovelycatv.sakurachat.entity.napcat.NapCatPrivateMessageEntity
import com.lovelycatv.sakurachat.repository.NapCatGroupMessageRepository
import com.lovelycatv.sakurachat.repository.NapCatPrivateMessageRepository
import com.lovelycatv.sakurachat.utils.EncryptUtils
import com.lovelycatv.sakurachat.utils.SnowIdGenerator
import com.lovelycatv.vertex.log.logger
import com.mikuac.shiro.core.Bot
import com.mikuac.shiro.core.BotPlugin
import com.mikuac.shiro.dto.event.message.GroupMessageEvent
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent
import org.springframework.stereotype.Component

@Component
class NapCatBotPlugin(
    private val napcatPrivateMessageRepository: NapCatPrivateMessageRepository,
    private val napCatGroupMessageRepository: NapCatGroupMessageRepository,
    private val snowIdGenerator: SnowIdGenerator
) : BotPlugin() {
    private val logger = logger()

    override fun onPrivateMessage(bot: Bot, event: PrivateMessageEvent): Int {
        napcatPrivateMessageRepository.save(
            NapCatPrivateMessageEntity(
                id = snowIdGenerator.nextId(),
                messageId = event.messageId,
                botId = bot.selfId,
                senderId = event.privateSender.userId,
                senderNickname = event.privateSender.nickname,
                message = event.arrayMsg.joinToString(),
                createdTime = event.time
            )
        )

        logger.info("Private message sent to bot ${bot.selfId} has been received and saved, message: $event")

        return super.onPrivateMessage(bot, event)
    }

    override fun onGroupMessage(bot: Bot, event: GroupMessageEvent): Int {
        val hash = EncryptUtils.calculateHash(event.rawMessage, "SHA-256")

        val existing = napCatGroupMessageRepository.getByHash(hash)

        if (existing != null) {
            logger.info("Found duplicated group message with hash $hash which has recorded by bot ${existing.botId}")
        } else {
            napCatGroupMessageRepository.save(
                NapCatGroupMessageEntity(
                    id = snowIdGenerator.nextId(),
                    messageId = event.messageId,
                    groupId = event.groupId,
                    botId = bot.selfId,
                    senderId = event.sender.userId,
                    senderNickname = event.sender.nickname,
                    message = event.arrayMsg.joinToString(),
                    createdTime = event.time,
                    hash = hash
                )
            )

            logger.info("Group message received and saved by bot ${bot.selfId}, message: $event")
        }

        return super.onGroupMessage(bot, event)
    }
}