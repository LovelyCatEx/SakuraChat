/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.service

import com.lovelycatv.sakurachat.core.im.message.AbstractMessage
import com.lovelycatv.vertex.ai.openai.ChatMessageRole
import com.lovelycatv.vertex.ai.openai.message.ChatMessage

interface AgentContextService {
    suspend fun getContextForChatCompletions(
        userId: Long,
        agentId: Long,
        channelId: Long
    ): List<ChatMessage>

    fun buildChatMessageFromAbstractMessage(
        message: AbstractMessage,
        role: ChatMessageRole
    ): ChatMessage
}