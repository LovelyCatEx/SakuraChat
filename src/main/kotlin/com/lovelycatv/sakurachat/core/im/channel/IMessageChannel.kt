/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.core.im.channel

import com.lovelycatv.sakurachat.core.im.message.AbstractMessage

interface IMessageChannel<M: IMessageChannelMember> {
    fun getChannelIdentifier(): Long

    fun addMember(member: M): M

    fun removeMember(member: M): Boolean

    fun listMembers(): List<M>

    fun getMemberById(id: String): M?

    suspend fun sendPrivateMessage(
        sender: M,
        receiver: M,
        message: AbstractMessage
    ): Boolean

    suspend fun sendGroupMessage(
        sender: M,
        message: AbstractMessage
    ): Boolean
}