/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.core

import com.lovelycatv.sakurachat.core.im.message.AbstractMessage
import com.lovelycatv.sakurachat.entity.aggregated.AggregatedAgentEntity
import com.lovelycatv.sakurachat.utils.toJSONString
import com.lovelycatv.vertex.log.logger
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SakuraChatAgent(
    val agent: AggregatedAgentEntity
) : ISakuraChatMessageChannelMember {
    companion object {
        const val MEMBER_PREFIX = "agent_"

        fun buildMemberId(agentId: Long) = "${MEMBER_PREFIX}$agentId"
    }

    private val logger = logger()

    override val memberId: String get() = buildMemberId(this.agent.agent.id!!)

    private val coroutineScope = CoroutineScope(
        Dispatchers.IO + CoroutineName("SakuraChatAgent#$memberId")
    )

    override fun onPrivateMessage(
        channel: SakuraChatMessageChannel,
        sender: ISakuraChatMessageChannelMember,
        message: AbstractMessage
    ) {
        println("Agent ${agent.agent.id} received message: ${message.toJSONString()}")

        coroutineScope.launch {
            channel.sendPrivateMessage(
                channel.getAgentMember(agent.agent.id!!)!!,
                sender,
                message
            )
        }
    }

    override fun onGroupMessage(
        channel: SakuraChatMessageChannel,
        sender: ISakuraChatMessageChannelMember,
        message: AbstractMessage
    ) {

    }
}