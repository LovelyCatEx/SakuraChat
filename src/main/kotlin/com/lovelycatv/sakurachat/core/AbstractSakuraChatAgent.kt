/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.core

import com.lovelycatv.sakurachat.core.task.MessageTask
import com.lovelycatv.sakurachat.core.task.MessageTaskQueueManager
import com.lovelycatv.sakurachat.entity.aggregated.AggregatedAgentEntity
import com.lovelycatv.sakurachat.types.ChannelMemberType
import com.lovelycatv.vertex.log.logger
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

abstract class AbstractSakuraChatAgent(
    val agent: AggregatedAgentEntity,
) : AbstractSakuraChatChannelMember(ChannelMemberType.AGENT) {
    private val logger = logger()

    override val id: Long get() = this.agent.agent.id

    // Supervisor job prevents exception blocking message handler tasks
    private val supervisorJob = SupervisorJob()
    // Name of coroutine
    private val coroutineName = CoroutineName("SakuraChatAgent#$memberId")
    // Exception handler
    private val coroutineExceptionHandler = CoroutineExceptionHandler { ctx, t ->
        logger.error("An error occurred while attempting to call agent ${agent.agent.id}#${agent.agent.name}", t)
    }

    protected val coroutineScope = CoroutineScope(
        supervisorJob + Dispatchers.IO + coroutineName + coroutineExceptionHandler
    )

    // Message task queue manager
    protected val messageTaskQueueManager = object : MessageTaskQueueManager(coroutineScope) {
        override suspend fun processTask(task: MessageTask) {
            logger.debug("Processing task {} for channel {}", task.uuid, task.channel.channelId)
            handleMessageTask(task)
        }
    }

    /**
     * Handle message task in subclass
     */
    protected abstract suspend fun handleMessageTask(task: MessageTask)
}
