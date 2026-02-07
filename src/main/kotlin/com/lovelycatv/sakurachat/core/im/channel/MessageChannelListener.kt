/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.core.im.channel

import com.lovelycatv.sakurachat.core.im.message.AbstractMessage

interface MessageChannelListener<C: IMessageChannel<M>, M: IMessageChannelMember> {
    fun onPrivateMessage(
        channel: C,
        sender: M,
        message: AbstractMessage
    )

    fun onGroupMessage(
        channel: C,
        sender: M,
        message: AbstractMessage
    )
}