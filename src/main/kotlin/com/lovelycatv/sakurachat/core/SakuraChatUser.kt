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
import com.lovelycatv.sakurachat.core.im.thirdparty.IThirdPartyIMAccessor
import com.lovelycatv.sakurachat.entity.UserEntity
import com.lovelycatv.sakurachat.types.ChannelMemberType
import com.lovelycatv.sakurachat.utils.toJSONString
import com.lovelycatv.vertex.log.logger
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SakuraChatUser(
    val user: UserEntity,
    val thirdPartyIMAccessorManager: ThirdPartyIMAccessorManager
) : AbstractSakuraChatChannelMember(ChannelMemberType.USER) {
    private val logger = logger()

    override val id: Long get() = this.user.id!!

    private val coroutineScope = CoroutineScope(
        Dispatchers.IO + CoroutineName("SakuraChatUser#$memberId")
    )

    override fun onPrivateMessage(
        channel: SakuraChatMessageChannel,
        sender: AbstractSakuraChatChannelMember,
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
        sender: AbstractSakuraChatChannelMember,
        message: AbstractMessage
    ) {
        handleMessage(sender, message) { extra ->
            sendGroupMessage(
                invoker = extra.platformInvoker,
                targetGroup = resolveGroupTargetByPlatformGroupId(extra.getPlatformGroupId())
                    ?: throw IllegalArgumentException("Could not resolve im accessor group target ${extra.platformAccountId} for platform ${extra.getPlatformType()}"),
                replyTarget = resolveTargetByPlatformAccountId(extra.platformAccountId)
                    ?: throw IllegalArgumentException("Could not resolve im accessor target ${extra.platformAccountId} for platform ${extra.getPlatformType()}"),
                message = message
            )
        }
    }

    private fun handleMessage(
        sender: ISakuraChatMessageChannelMember,
        message: AbstractMessage,
        accessorActions: suspend IThirdPartyIMAccessor<Any, Any, Any>.(SakuraChatMessageExtra) -> Unit
    ) {
        println("User ${user.id} received message from sender ${sender.memberId}: ${message.toJSONString()}")

        if (SakuraChatMessageExtra.isCapable(message.extraBody)) {
            try {
                val imAccessor = thirdPartyIMAccessorManager.getAccessor(message.extraBody.getPlatformType())
                    ?: throw IllegalArgumentException("No IM Accessor for ${message.extraBody.getPlatformType()}")

                coroutineScope.launch {
                    try {
                        accessorActions.invoke(imAccessor, message.extraBody)
                    } catch (e: Exception) {
                        logger.error("Could not send message received by channel user $memberId, accessor: ${imAccessor::class.qualifiedName}, data: ${message.toJSONString()}", e)
                    }
                }
            } catch (e: Exception) {
                logger.error("Could not handle message received by channel user $memberId", e)
            }
        } else {
            logger.warn("User ${user.id} received a message but not seem like a valid SakuraChat Message, data: ${message.toJSONString()}")
        }
    }
}