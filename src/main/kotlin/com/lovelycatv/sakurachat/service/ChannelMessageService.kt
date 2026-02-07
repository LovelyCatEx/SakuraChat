/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.service

import com.lovelycatv.sakurachat.core.ISakuraChatMessageChannelMember
import com.lovelycatv.sakurachat.core.SakuraChatMessageChannel
import com.lovelycatv.sakurachat.core.im.message.AbstractMessage
import com.lovelycatv.sakurachat.repository.ChannelMessageRepository

interface ChannelMessageService : BaseService<ChannelMessageRepository> {
    fun saveMessage(
        channel: SakuraChatMessageChannel,
        sender: ISakuraChatMessageChannelMember,
        message: AbstractMessage
    )
}