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

abstract class AbstractMessageChannel(
    val channelId: Long
) : IMessageChannel {
    private val logger = logger()
    private val members: MutableList<IMessageChannelMember> = mutableListOf()
    private val listeners: MutableMap<IMessageChannelMember, MutableList<MessageChannelListener>> = mutableMapOf()

    override fun getChannelIdentifier(): Long = this.channelId

    override fun addMember(member: IMessageChannelMember): IMessageChannelMember {
        if (this.getMemberById(member.memberId) == null) {
            this.members.add(member)
        }

        return member
    }

    override fun removeMember(member: IMessageChannelMember): Boolean {
        return this.members.remove(member)
    }

    override fun listMembers(): List<IMessageChannelMember> {
        return this.members
    }

    override fun getMemberById(id: String): IMessageChannelMember? {
        return this.listMembers().find { it.memberId == id }
    }

    override suspend fun sendPrivateMessage(
        sender: IMessageChannelMember,
        receiver: IMessageChannelMember,
        message: AbstractMessage
    ): Boolean {
        if (sender == receiver) {
            throw IllegalArgumentException("Sender and receiver cannot be the same, senderMemberId: ${sender.memberId}")
        }
        return try {
            this.getListeners(receiver).forEach {
                it.onPrivateMessage(this, sender, message)
            }
            true
        } catch (e: Exception) {
            logger.error("Exception occurred when sending message", e)
            false
        }
    }

    override suspend fun sendGroupMessage(
        sender: IMessageChannelMember,
        message: AbstractMessage
    ): Boolean {
        return try {
            this.getListenersExcept(sender).forEach {
                it.onGroupMessage(this, sender, message)
            }
            true
        } catch (e: Exception) {
            logger.error("Exception occurred when sending message", e)
            false
        }
    }

    override fun getListeners(member: IMessageChannelMember): List<MessageChannelListener> {
        return this.listeners[member] ?: listOf()
    }

    fun getListenersExcept(exceptMember: IMessageChannelMember): List<MessageChannelListener> {
        return this.getListenersExcept(listOf(exceptMember))
    }

    fun getListenersExcept(exceptMembers: List<IMessageChannelMember>): List<MessageChannelListener> {
        return this.listeners.filterNot { it.key in exceptMembers }.values.flatten()
    }

    override fun registerListener(
        member: IMessageChannelMember,
        listener: MessageChannelListener
    ) {
        val list = this.listeners.getOrPut(member) { mutableListOf() }
        if (!list.contains(listener)) {
            list.add(listener)
        }
    }

    override fun unregisterListener(
        member: IMessageChannelMember,
        listener: MessageChannelListener
    ) {
        val list = this.listeners.getOrPut(member) { mutableListOf() }
        list.remove(listener)
    }

    override fun unregisterAllListeners(member: IMessageChannelMember) {
        this.listeners.remove(member)
    }
}