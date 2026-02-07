/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.service.impl

import com.lovelycatv.sakurachat.entity.channel.AgentChannelRelationEntity
import com.lovelycatv.sakurachat.entity.channel.IMChannelEntity
import com.lovelycatv.sakurachat.entity.channel.UserChannelRelationEntity
import com.lovelycatv.sakurachat.repository.AgentChannelRelationRepository
import com.lovelycatv.sakurachat.repository.IMChannelRepository
import com.lovelycatv.sakurachat.repository.UserChannelRelationRepository
import com.lovelycatv.sakurachat.service.IMChannelService
import com.lovelycatv.sakurachat.types.ChannelType
import com.lovelycatv.sakurachat.utils.SnowIdGenerator
import com.lovelycatv.vertex.log.logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class IMChannelServiceImpl(
    private val imChannelRepository: IMChannelRepository,
    private val userChannelRelationRepository: UserChannelRelationRepository,
    private val agentChannelRelationRepository: AgentChannelRelationRepository,
    private val snowIdGenerator: SnowIdGenerator
) : IMChannelService {
    private val logger = logger()

    override fun getRepository(): IMChannelRepository {
        return this.imChannelRepository
    }

    @Transactional
    override suspend fun getOrCreatePrivateChannelByUserIdAndAgentId(
        userId: Long,
        agentId: Long
    ): IMChannelEntity {
        val userChannels = withContext(Dispatchers.IO) {
            userChannelRelationRepository.findByUserId(userId)
        }

        val agentChannels = withContext(Dispatchers.IO) {
            agentChannelRelationRepository.findByAgentId(agentId)
        }

        val crossChannelIds = userChannels.map {
            it.primaryKey.channelId
        }.toSet().intersect(agentChannels.map {
            it.primaryKey.channelId
        }.toSet())

        return withContext(Dispatchers.IO) {
            getRepository()
                .findAllById(crossChannelIds)
                .firstOrNull {
                    it.getRealChannelType() == ChannelType.PRIVATE
                }
                ?:
                // Create
                getRepository().save(
                    IMChannelEntity(
                        id = snowIdGenerator.nextId(),
                        channelName = "private:${userId}:${agentId}",
                        channelIdentifier = "private:${userId}:${agentId}",
                        channelType = ChannelType.PRIVATE.channelTypeId,
                    )
                ).also {
                    userChannelRelationRepository.save(
                        UserChannelRelationEntity(
                            primaryKey = UserChannelRelationEntity.PrimaryKey(
                                channelId = it.id,
                                userId = userId
                            )
                        )
                    )

                    agentChannelRelationRepository.save(
                        AgentChannelRelationEntity(
                            primaryKey = AgentChannelRelationEntity.PrimaryKey(
                                channelId = it.id,
                                agentId = agentId
                            )
                        )
                    )

                    logger.info("A private IM Channel created for user: $userId and agent: $agentId")
                }
        }
    }

    override suspend fun getOrCreateGroupChannelByUserIdAndAgentId(
        groupId: String,
        userId: Long,
        agentId: Long
    ): IMChannelEntity {
        val expectChannelIdentifier = "group:${groupId}"

        return withContext(Dispatchers.IO) {
            getRepository()
                .findByChannelIdentifier(expectChannelIdentifier)
                .firstOrNull {
                    it.getRealChannelType() == ChannelType.GROUP
                }
                .also { foundChannel ->
                    if (foundChannel == null) {
                        return@also
                    }

                    // Even though the channel is existed (created by other user),
                    // the user may not be in this channel
                    val userIdsInThisChannel = userChannelRelationRepository
                        .findByChannelId(foundChannel.id)
                        .map { it.primaryKey.userId }
                        .toSet()

                    if (userId !in userIdsInThisChannel) {
                        userChannelRelationRepository.save(
                            UserChannelRelationEntity(
                                primaryKey = UserChannelRelationEntity.PrimaryKey(
                                    channelId = foundChannel.id,
                                    userId = userId
                                )
                            )
                        )

                        logger.info("New user $userId joined group channel: ${foundChannel.id}")
                    }
                }
                ?:
                getRepository().save(
                    IMChannelEntity(
                        id = snowIdGenerator.nextId(),
                        channelName = expectChannelIdentifier,
                        channelIdentifier = expectChannelIdentifier,
                        channelType = ChannelType.GROUP.channelTypeId,
                    )
                ).also {
                    userChannelRelationRepository.save(
                        UserChannelRelationEntity(
                            primaryKey = UserChannelRelationEntity.PrimaryKey(
                                channelId = it.id,
                                userId = userId
                            )
                        )
                    )

                    agentChannelRelationRepository.save(
                        AgentChannelRelationEntity(
                            primaryKey = AgentChannelRelationEntity.PrimaryKey(
                                channelId = it.id,
                                agentId = agentId
                            )
                        )
                    )

                    logger.info("A group IM Channel created for user: $userId and agent: $agentId")
                }

        }
    }
}