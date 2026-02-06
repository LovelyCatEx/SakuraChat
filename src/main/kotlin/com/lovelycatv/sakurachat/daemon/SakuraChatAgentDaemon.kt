/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.daemon

import com.lovelycatv.sakurachat.core.im.channel.IMessageChannel
import com.lovelycatv.sakurachat.core.im.channel.IMessageChannelMember
import com.lovelycatv.sakurachat.core.im.channel.MessageChannelListener
import com.lovelycatv.sakurachat.core.im.channel.SimpleMessageChannel
import com.lovelycatv.sakurachat.core.im.message.AbstractMessage
import com.lovelycatv.sakurachat.repository.AgentChannelRelationRepository
import com.lovelycatv.sakurachat.repository.AgentRepository
import com.lovelycatv.sakurachat.service.IMChannelService
import com.lovelycatv.sakurachat.utils.toJSONString
import com.lovelycatv.vertex.log.logger
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class SakuraChatAgentDaemon(
    private val agentRepository: AgentRepository,
    private val imChannelService: IMChannelService
) {
    private val logger = logger()

    private val privateChannels: MutableMap<Long, MutableMap<Long, IMessageChannel>> = mutableMapOf()

    suspend fun getPrivateMessageChannel(
        agentId: Long,
        userId: Long,
        agentMember: IMessageChannelMember,
        userMember: IMessageChannelMember
    ): IMessageChannel {
        return privateChannels[agentId]?.get(userId) ?: run {
            val channel = imChannelService.getOrCreateChannelByUserIdAndAgentId(userId, agentId)

            SimpleMessageChannel(
                channelId = channel.id
            ).also {
                it.addMember(agentMember)
                it.addMember(userMember)

                it.registerListener(agentMember, object : MessageChannelListener {
                    override fun onPrivateMessage(
                        channel: IMessageChannel,
                        sender: IMessageChannelMember,
                        message: AbstractMessage
                    ) {
                        println("Agent $agentId received message: ${message.toJSONString()}")
                    }

                    override fun onGroupMessage(
                        channel: IMessageChannel,
                        sender: IMessageChannelMember,
                        message: AbstractMessage
                    ) {

                    }
                })

                it.registerListener(userMember, object : MessageChannelListener {
                    override fun onPrivateMessage(
                        channel: IMessageChannel,
                        sender: IMessageChannelMember,
                        message: AbstractMessage
                    ) {
                        println("User $userId received message: ${message.toJSONString()}")
                    }

                    override fun onGroupMessage(
                        channel: IMessageChannel,
                        sender: IMessageChannelMember,
                        message: AbstractMessage
                    ) {

                    }
                })

                privateChannels.getOrPut(agentId) {
                    mutableMapOf()
                }[userId] = it
            }
        }
    }

    @Scheduled(cron = "0 */5 * * * *")
    fun check() {
        logger.info("Checking agents <=> channels")

        val allAgents = agentRepository.findAll()
    }
}