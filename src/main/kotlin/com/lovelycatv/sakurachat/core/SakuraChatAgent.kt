/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.core

import com.lovelycatv.sakurachat.core.im.channel.IMessageChannelMember
import com.lovelycatv.sakurachat.entity.aggregated.AggregatedAgentEntity

class SakuraChatAgent(
    val agent: AggregatedAgentEntity
) : IMessageChannelMember {
    override val memberId: String get() = "agent_" + this.agent.agent.id!!


}