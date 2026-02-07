/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.daemon

import com.lovelycatv.sakurachat.core.ISakuraChatMessageChannelListener
import com.lovelycatv.sakurachat.core.ISakuraChatMessageChannelMember
import com.lovelycatv.sakurachat.core.SakuraChatAgentInstanceManager
import com.lovelycatv.sakurachat.core.SakuraChatMessageChannel
import com.lovelycatv.sakurachat.core.SakuraChatUserInstanceManager
import com.lovelycatv.sakurachat.core.im.message.AbstractMessage
import com.lovelycatv.sakurachat.entity.UserEntity
import com.lovelycatv.sakurachat.entity.aggregated.AggregatedAgentEntity
import com.lovelycatv.sakurachat.repository.AgentRepository
import com.lovelycatv.sakurachat.service.ChannelMessageService
import com.lovelycatv.sakurachat.service.IMChannelService
import com.lovelycatv.vertex.log.logger
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class SakuraChatMessageChannelDaemon(
    private val agentRepository: AgentRepository,
    private val imChannelService: IMChannelService,
    private val sakuraChatAgentInstanceManager: SakuraChatAgentInstanceManager,
    private val sakuraChatUserInstanceManager: SakuraChatUserInstanceManager,
    private val channelMessageService: ChannelMessageService
) {
    private val logger = logger()

    private val privateChannels: MutableMap<Long, MutableMap<Long, SakuraChatMessageChannel>> = mutableMapOf()
    private val groupChannels: MutableMap<Long, SakuraChatMessageChannel> = mutableMapOf()

    suspend fun getPrivateMessageChannel(
        agent: AggregatedAgentEntity,
        user: UserEntity,
    ): SakuraChatMessageChannel {
        val agentId = agent.agent.id!!
        val userId = user.id!!
        return privateChannels[agentId]?.get(userId) ?: run {
            val channel = imChannelService.getOrCreatePrivateChannelByUserIdAndAgentId(userId, agentId)

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

                it.registerListener(agentMember, agentMember)

                it.registerListener(userMember, userMember)

                applyChannelMessageSaverInterceptor(it)

                privateChannels.getOrPut(agentId) {
                    mutableMapOf()
                }[userId] = it
            }
        }
    }

    suspend fun getGroupMessageChannel(
        thirdPartyGroupEntityId: Long,
        agent: AggregatedAgentEntity,
        user: UserEntity
    ): SakuraChatMessageChannel {
        val agentId = agent.agent.id!!
        val userId = user.id!!

        val channel = groupChannels[thirdPartyGroupEntityId] ?: run {
            val channel = imChannelService.getOrCreateGroupChannelByUserIdAndAgentId(
                thirdPartyGroupEntityId,
                userId,
                agentId
            )

            SakuraChatMessageChannel(
                channelId = channel.id
            ).also {
                val agentMember = it.addMember(
                    with(sakuraChatAgentInstanceManager) {
                        getAgent(agentId) ?: addAgent(agent)
                    }
                )
                it.registerListener(agentMember, agentMember)

                groupChannels.getOrPut(thirdPartyGroupEntityId) { it }
            }
        }

        // Even though the channel is existed (created by other user),
        // the user may not be in this channel
        if (channel.getUserMember(userId) == null) {
            val userMember = channel.addMember(
                with(sakuraChatUserInstanceManager) {
                    getUser(userId) ?: addUser(user)
                }
            )

            channel.registerListener(userMember, userMember)
        }

        applyChannelMessageSaverInterceptor(channel)

        return channel
    }

    private fun applyChannelMessageSaverInterceptor(
        channel: SakuraChatMessageChannel
    ) {
        val listener = object : ISakuraChatMessageChannelListener {
            override fun onPrivateMessage(
                channel: SakuraChatMessageChannel,
                sender: ISakuraChatMessageChannelMember,
                message: AbstractMessage
            ) {
                channelMessageService.saveMessage(channel, sender, message)
            }

            override fun onGroupMessage(
                channel: SakuraChatMessageChannel,
                sender: ISakuraChatMessageChannelMember,
                message: AbstractMessage
            ) {
                channelMessageService.saveMessage(channel, sender, message)
            }

        }

        // Intercept all members' message
        channel.listMembers().forEach {
            channel.registerListener(it, listener)
        }
    }

    @Scheduled(cron = "0 */5 * * * *")
    fun check() {
        logger.info("Checking agents <=> channels")

        val allAgents = agentRepository.findAll()
    }
}