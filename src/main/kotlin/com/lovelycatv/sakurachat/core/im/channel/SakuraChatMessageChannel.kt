/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.core.im.channel

class SakuraChatMessageChannel(
    channelId: Long
) : AbstractMessageChannel(channelId) {
    fun getAgentMember(agentId: Long): IMessageChannelMember? {
        return super.getMemberById("agent_$agentId")
    }

    fun getUserMember(userId: Long): IMessageChannelMember? {
        return super.getMemberById("user_$userId")
    }
}