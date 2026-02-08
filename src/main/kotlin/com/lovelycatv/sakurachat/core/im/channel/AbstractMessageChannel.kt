/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.core.im.channel

import com.lovelycatv.sakurachat.core.im.message.AbstractMessage
import com.lovelycatv.vertex.log.logger

abstract class AbstractMessageChannel<
        C: IMessageChannel<M>,
        M: IMessageChannelMember,
        L: MessageChannelListener<C, M>
>(
    val channelId: Long
) : ListenableMessageChannel<C, M, L>() {
    private val logger = logger()
    private val members: MutableList<M> = mutableListOf()

    override fun getChannelIdentifier(): Long = this.channelId

    override fun addMember(member: M): M {
        if (this.getMemberById(member.memberId) == null) {
            this.members.add(member)
        }

        return member
    }

    override fun removeMember(member: M): Boolean {
        return this.members.remove(member)
    }

    override fun listMembers(): List<M> {
        return this.members
    }

    override fun getMemberById(id: String): M? {
        return this.listMembers().find { it.memberId == id }
    }

    override suspend fun sendPrivateMessage(
        sender: M,
        receiver: M,
        message: AbstractMessage
    ): Boolean {
        if (sender == receiver) {
            throw IllegalArgumentException("Sender and receiver cannot be the same, senderMemberId: ${sender.memberId}")
        }

        if (message.isEmpty()) {
            throw IllegalArgumentException("Message cannot be empty")
        }

        return try {
            this.getListeners(receiver).forEach {
                @Suppress("UNCHECKED_CAST")
                it.onPrivateMessage(this as C, sender, message)
            }
            true
        } catch (e: Exception) {
            logger.error("Exception occurred when sending message", e)
            false
        }
    }

    override suspend fun sendGroupMessage(
        sender: M,
        message: AbstractMessage
    ): Boolean {
        if (message.isEmpty()) {
            throw IllegalArgumentException("Message cannot be empty")
        }

        return try {
            this.getListenersExcept(sender).forEach {
                @Suppress("UNCHECKED_CAST")
                it.onGroupMessage(this as C, sender, message)
            }
            true
        } catch (e: Exception) {
            logger.error("Exception occurred when sending message", e)
            false
        }
    }
}