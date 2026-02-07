/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.core.im.channel

abstract class ListenableMessageChannel<
        C: IMessageChannel<M>,
        M: IMessageChannelMember,
        L: MessageChannelListener<C, M>
> : IMessageChannel<M> {
    private val listeners: MutableMap<M, MutableList<L>> = mutableMapOf()

    fun getListeners(member: M): List<L> {
        return this.listeners[member] ?: listOf()
    }

    fun getListenersExcept(exceptMember: M): List<L> {
        return this.getListenersExcept(listOf(exceptMember))
    }

    fun getListenersExcept(exceptMembers: List<M>): List<L> {
        return this.listeners.filterNot { it.key in exceptMembers }.values.flatten()
    }

    fun registerListener(
        member: M,
        listener: L
    ) {
        val list = this.listeners.getOrPut(member) { mutableListOf() }
        if (!list.contains(listener)) {
            list.add(listener)
        }
    }

    fun unregisterListener(
        member: M,
        listener: L
    ) {
        val list = this.listeners.getOrPut(member) { mutableListOf() }
        list.remove(listener)
    }

    fun unregisterAllListeners(member: M) {
        this.listeners.remove(member)
    }
}