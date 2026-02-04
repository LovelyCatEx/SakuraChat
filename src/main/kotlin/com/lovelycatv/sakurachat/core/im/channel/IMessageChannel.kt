/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.core.im.channel

import com.lovelycatv.sakurachat.core.im.message.AbstractMessage

interface IMessageChannel {
    fun getChannelIdentifier(): Long

    fun addMember(member: IMessageChannelMember)

    fun removeMember(member: IMessageChannelMember)

    fun listMembers(): List<IMessageChannelMember>

    fun getMemberById(id: Long): IMessageChannelMember?

    suspend fun sendPrivateMessage(
        sender: IMessageChannelMember,
        receiver: IMessageChannelMember,
        message: AbstractMessage
    ): Boolean

    suspend fun sendGroupMessage(
        sender: IMessageChannelMember,
        message: AbstractMessage
    ): Boolean

    fun getListeners(member: IMessageChannelMember): List<MessageChannelListener>

    fun registerListener(member: IMessageChannelMember, listener: MessageChannelListener)

    fun unregisterListener(member: IMessageChannelMember, listener: MessageChannelListener)

    fun unregisterAllListeners(member: IMessageChannelMember)
}