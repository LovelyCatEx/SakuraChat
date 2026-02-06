/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.daemon

import com.lovelycatv.sakurachat.core.SakuraChatAgentInstanceManager
import com.lovelycatv.sakurachat.core.SakuraChatUserInstanceManager
import com.lovelycatv.sakurachat.core.im.channel.IMessageChannel
import com.lovelycatv.sakurachat.core.im.channel.IMessageChannelMember
import com.lovelycatv.sakurachat.core.im.channel.MessageChannelListener
import com.lovelycatv.sakurachat.core.im.channel.SakuraChatMessageChannel
import com.lovelycatv.sakurachat.core.im.message.AbstractMessage
import com.lovelycatv.sakurachat.entity.UserEntity
import com.lovelycatv.sakurachat.entity.aggregated.AggregatedAgentEntity
import com.lovelycatv.sakurachat.repository.AgentRepository
import com.lovelycatv.sakurachat.service.IMChannelService
import com.lovelycatv.vertex.log.logger
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class SakuraChatAgentDaemon(
    private val agentRepository: AgentRepository,
    private val imChannelService: IMChannelService,
    private val sakuraChatAgentInstanceManager: SakuraChatAgentInstanceManager,
    private val sakuraChatUserInstanceManager: SakuraChatUserInstanceManager
) {
    private val logger = logger()

    private val privateChannels: MutableMap<Long, MutableMap<Long, SakuraChatMessageChannel>> = mutableMapOf()

    suspend fun getPrivateMessageChannel(
        agent: AggregatedAgentEntity,
        user: UserEntity,
        agentListener: MessageChannelListener = object : MessageChannelListener {
            override fun onPrivateMessage(
                channel: IMessageChannel,
                sender: IMessageChannelMember,
                message: AbstractMessage
            ) {
                logger.warn("Agent ${agent.agent.id} is not listening the channel ${channel.getChannelIdentifier()} on private messages")
            }
            override fun onGroupMessage(
                channel: IMessageChannel,
                sender: IMessageChannelMember,
                message: AbstractMessage
            ) {
                logger.warn("Agent ${agent.agent.id} is not listening the channel ${channel.getChannelIdentifier()} on group messages")
            }
        },
        userListener: MessageChannelListener = object : MessageChannelListener {
            override fun onPrivateMessage(
                channel: IMessageChannel,
                sender: IMessageChannelMember,
                message: AbstractMessage
            ) {
                logger.warn("User ${agent.agent.id} is not listening the channel ${channel.getChannelIdentifier()} on private messages")
            }
            override fun onGroupMessage(
                channel: IMessageChannel,
                sender: IMessageChannelMember,
                message: AbstractMessage
            ) {
                logger.warn("User ${agent.agent.id} is not listening the channel ${channel.getChannelIdentifier()} on group messages")
            }
        }
    ): SakuraChatMessageChannel {
        val agentId = agent.agent.id!!
        val userId = user.id!!
        return privateChannels[agentId]?.get(userId) ?: run {
            val channel = imChannelService.getOrCreateChannelByUserIdAndAgentId(userId, agentId)

            SakuraChatMessageChannel(
                channelId = channel.id
            ).also {
                val agentMember = it.addMember(
                    with(sakuraChatAgentInstanceManager) {
                        getAgent(agentId) ?: addAgent(agent)
                    }
                )

                val userMember = it.addMember(
                    with(sakuraChatUserInstanceManager) {
                        getUser(userId) ?: addUser(user)
                    }
                )

                it.registerListener(agentMember, agentListener)

                it.registerListener(userMember, userListener)

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