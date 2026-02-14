package com.lovelycatv.sakurachat.core.task

import com.lovelycatv.sakurachat.core.AbstractSakuraChatChannelMember
import com.lovelycatv.sakurachat.core.SakuraChatMessageChannel
import com.lovelycatv.sakurachat.core.im.message.AbstractMessage
import java.util.*

sealed class MessageTask(
    val uuid: String,
    open val channel: SakuraChatMessageChannel,
    open val sender: AbstractSakuraChatChannelMember,
) {
    data class SendPrivateMessage(
        override val channel: SakuraChatMessageChannel,
        override val sender: AbstractSakuraChatChannelMember,
        val recipient: AbstractSakuraChatChannelMember,
        val message: AbstractMessage,
    ) : MessageTask(UUID.randomUUID().toString(), channel, sender)

    data class SendGroupMessage(
        override val channel: SakuraChatMessageChannel,
        override val sender: AbstractSakuraChatChannelMember,
        val message: AbstractMessage,
    ) : MessageTask(UUID.randomUUID().toString(), channel, sender)
}

