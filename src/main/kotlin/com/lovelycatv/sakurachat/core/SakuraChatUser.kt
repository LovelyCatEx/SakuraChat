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
import com.lovelycatv.sakurachat.core.im.message.TextMessage
import com.lovelycatv.sakurachat.core.im.thirdparty.IThirdPartyIMAccessor
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
        handleMessage(sender, message) { extra ->
            sendPrivateMessage(
                invoker = extra.platformInvoker,
                target = resolveTargetByPlatformAccountId(extra.platformAccountId)
                    ?: throw IllegalArgumentException("Could not resolve im accessor target ${extra.platformAccountId} for platform ${extra.getPlatformType()}"),
                message = message
            )
        }
    }

    override fun onGroupMessage(
        channel: SakuraChatMessageChannel,
        sender: ISakuraChatMessageChannelMember,
        message: AbstractMessage
    ) {
        handleMessage(sender, message) { extra ->
            sendPrivateMessage(
                invoker = extra.platformInvoker,
                target = resolveTargetByPlatformAccountId(extra.platformAccountId)
                    ?: throw IllegalArgumentException("Could not resolve im accessor target ${extra.platformAccountId} for platform ${extra.getPlatformType()}"),
                message = TextMessage(
                    sequence = message.sequence,
                    message = "Group message reply is not supported yet.",
                    extraBody = extra
                )
            )
        }
    }

    private fun handleMessage(
        sender: ISakuraChatMessageChannelMember,
        message: AbstractMessage,
        accessorActions: suspend IThirdPartyIMAccessor<Any, Any>.(SakuraChatMessageExtra) -> Unit
    ) {
        println("User ${user.id} received message from sender ${sender.memberId}: ${message.toJSONString()}")
        if (SakuraChatMessageExtra.isCapable(message.extraBody)) {
            val imAccessor = thirdPartyIMAccessorManager.getAccessor(message.extraBody.getPlatformType())
                ?: throw IllegalArgumentException("No IM Accessor for ${message.extraBody.getPlatformType()}")

            coroutineScope.launch {
                accessorActions.invoke(imAccessor, message.extraBody)
            }
        } else {
            logger.warn("User ${user.id} received a message but not seem like a valid SakuraChat Message, data: ${message.toJSONString()}")
        }
    }
}