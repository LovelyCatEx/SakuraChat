/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.core

import com.lovelycatv.sakurachat.core.im.channel.AbstractMessageChannel

class SakuraChatMessageChannel(
    channelId: Long
) : AbstractMessageChannel<SakuraChatMessageChannel, ISakuraChatMessageChannelMember, ISakuraChatMessageChannelListener>(channelId) {
    fun getAgentMember(agentId: Long): ISakuraChatMessageChannelMember? {
        return super.getMemberById("agent_$agentId")
    }

    fun getUserMember(userId: Long): ISakuraChatMessageChannelMember? {
        return super.getMemberById("user_$userId")
    }
}