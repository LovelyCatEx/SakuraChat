/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.core

import com.lovelycatv.sakurachat.core.im.channel.AbstractMessageChannel
import com.lovelycatv.sakurachat.types.ChannelMemberType

class SakuraChatMessageChannel(
    channelId: Long
) : AbstractMessageChannel<
        SakuraChatMessageChannel,
        AbstractSakuraChatChannelMember,
        ISakuraChatMessageChannelListener
>(
    channelId
) {
    fun getAgentMember(agentId: Long): AbstractSakuraChatChannelMember? {
        return super.getMemberById(
            AbstractSakuraChatChannelMember.buildMemberId(ChannelMemberType.AGENT, agentId)
        )
    }

    fun getUserMember(userId: Long): AbstractSakuraChatChannelMember? {
        return super.getMemberById(
            AbstractSakuraChatChannelMember.buildMemberId(ChannelMemberType.USER, userId)
        )
    }
}