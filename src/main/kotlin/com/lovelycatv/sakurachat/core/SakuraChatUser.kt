/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.core

import com.lovelycatv.sakurachat.adapters.thirdparty.im.ThirdPartyIMAccessorManager
import com.lovelycatv.sakurachat.core.im.message.AbstractMessage
import com.lovelycatv.sakurachat.entity.UserEntity
import com.lovelycatv.sakurachat.utils.toJSONString
import com.lovelycatv.vertex.log.logger
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SakuraChatUser(
    val user: UserEntity,
    val thirdPartyIMAccessorManager: ThirdPartyIMAccessorManager
) : ISakuraChatMessageChannelMember {
    companion object {
        const val MEMBER_PREFIX = "user_"

        fun buildMemberId(userId: Long) = "${MEMBER_PREFIX}$userId"
    }

    private val logger = logger()

    override val memberId: String get() = buildMemberId(this.user.id!!)

    private val coroutineScope = CoroutineScope(
        Dispatchers.IO + CoroutineName("SakuraChatUser#$memberId")
    )

    override fun onPrivateMessage(
        channel: SakuraChatMessageChannel,
        sender: ISakuraChatMessageChannelMember,
        message: AbstractMessage
    ) {
        println("User ${user.id} received message from sender ${sender.memberId}: ${message.toJSONString()}")
        if (SakuraChatMessageExtra.isCapable(message.extraBody)) {
            val imAccessor = thirdPartyIMAccessorManager.getAccessor(message.extraBody.getPlatformType())
                ?: throw IllegalArgumentException("No IM Accessor for ${message.extraBody.getPlatformType()}")

            coroutineScope.launch {
                imAccessor.sendPrivateMessage(
                    invoker = message.extraBody.platformInvoker,
                    target = imAccessor.resolveTargetByPlatformAccountId(message.extraBody.platformAccountId)
                        ?: throw IllegalArgumentException("Could not resolve im accessor target ${message.extraBody.platformAccountId} for platform ${message.extraBody.getPlatformType()}"),
                    message = message
                )
            }
        } else {
            logger.warn("User ${user.id} received a message but not seem like a valid SakuraChat Message, data: ${message.toJSONString()}")
        }
    }

    override fun onGroupMessage(
        channel: SakuraChatMessageChannel,
        sender: ISakuraChatMessageChannelMember,
        message: AbstractMessage
    ) {
        TODO("Not yet implemented")
    }
}