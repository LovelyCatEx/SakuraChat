/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.core

import com.lovelycatv.sakurachat.core.im.channel.IMessageChannelMember

class SakuraChatAgent(
    val agentId: Long,
    val agentName: String,
) : IMessageChannelMember {
    override val memberId: Long get() = this.agentId


}