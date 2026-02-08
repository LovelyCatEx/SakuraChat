/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.adapters.thirdparty.im

import com.lovelycatv.sakurachat.core.im.message.AbstractMessage
import com.lovelycatv.sakurachat.core.im.message.TextMessage
import com.lovelycatv.sakurachat.core.im.thirdparty.IThirdPartyIMAccessor
import com.lovelycatv.sakurachat.entity.napcat.NapCatGroupMessageEntity
import com.lovelycatv.sakurachat.entity.napcat.NapCatPrivateMessageEntity
import com.lovelycatv.sakurachat.repository.NapCatGroupMessageRepository
import com.lovelycatv.sakurachat.repository.NapCatPrivateMessageRepository
import com.lovelycatv.sakurachat.types.ThirdPartyPlatform
import com.lovelycatv.sakurachat.utils.SnowIdGenerator
import com.lovelycatv.sakurachat.utils.toJSONString
import com.lovelycatv.vertex.log.logger
import com.mikuac.shiro.common.utils.MsgUtils
import com.mikuac.shiro.common.utils.ShiroUtils
import com.mikuac.shiro.core.Bot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Component

@Component
class NapCatIMAccessor(
    private val napcatPrivateMessageRepository: NapCatPrivateMessageRepository,
    private val napCatGroupMessageRepository: NapCatGroupMessageRepository,
    private val snowIdGenerator: SnowIdGenerator
) : IThirdPartyIMAccessor<Bot, Long, Long> {
    private val logger = logger()

    override fun getPlatform(): ThirdPartyPlatform {
        return ThirdPartyPlatform.NAPCAT_OICQ
    }

    override suspend fun sendPrivateMessage(
        invoker: Bot,
        target: Long,
        message: AbstractMessage
    ): Boolean {
        if (message.isEmpty()) {
            logger.warn("sendPrivateMessage skipped due to empty message")
            return false
        }

        val msg = MsgUtils.builder().apply {
            if (message is TextMessage) {
                text(message.message)
            } else {
                text(message.toJSONString())
            }
        }.build()

        val result = invoker.sendPrivateMsg(target, msg, false)

        withContext(Dispatchers.IO) {
            napcatPrivateMessageRepository.save(
                NapCatPrivateMessageEntity(
                    id = snowIdGenerator.nextId(),
                    botId = target,
                    senderId = invoker.selfId,
                    senderNickname = invoker.selfId.toString(),
                    messageId = result.data.messageId,
                    message = ShiroUtils.arrayMsgToCode(ShiroUtils.rawToArrayMsg(msg)),
                    createdTime = System.currentTimeMillis()
                )
            )
        }

        return result?.retCode == 0
    }

    override suspend fun sendGroupMessage(
        invoker: Bot,
        targetGroup: Long,
        replyTarget: Long,
        message: AbstractMessage
    ): Boolean {
        if (message.isEmpty()) {
            logger.warn("sendGroupMessage skipped due to empty message")
            return false
        }

        val msg = if (message is TextMessage) {
            MsgUtils
                .builder()
                .at(replyTarget)
                .text(" ")
                .text(message.message)
                .build()
        } else {
            MsgUtils
                .builder()
                .at(replyTarget)
                .text(" ")
                .text(message.toJSONString())
                .build()
        }

        val result = invoker.sendGroupMsg(targetGroup, msg, false)

        withContext(Dispatchers.IO) {
            napCatGroupMessageRepository.save(
                NapCatGroupMessageEntity(
                    id = snowIdGenerator.nextId(),
                    botId = replyTarget,
                    groupId = targetGroup,
                    senderId = invoker.selfId,
                    senderNickname = invoker.selfId.toString(),
                    messageId = result.data.messageId,
                    message = ShiroUtils.arrayMsgToCode(ShiroUtils.rawToArrayMsg(msg)),
                    createdTime = System.currentTimeMillis()
                )
            )
        }

        return result?.retCode == 0
    }

    override suspend fun resolveTargetByPlatformAccountId(platformAccountId: String): Long? {
        return platformAccountId.toLong()
    }

    override suspend fun resolveGroupTargetByPlatformGroupId(platformGroupId: String): Long? {
        return platformGroupId.toLong()
    }
}