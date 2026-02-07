/*
 * Copyright 2025 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.napcat

import com.lovelycatv.sakurachat.core.SakuraChatMessageExtra
import com.lovelycatv.sakurachat.core.im.message.TextMessage
import com.lovelycatv.sakurachat.daemon.SakuraChatMessageChannelDaemon
import com.lovelycatv.sakurachat.entity.napcat.NapCatGroupMessageEntity
import com.lovelycatv.sakurachat.entity.napcat.NapCatPrivateMessageEntity
import com.lovelycatv.sakurachat.repository.NapCatGroupMessageRepository
import com.lovelycatv.sakurachat.repository.NapCatPrivateMessageRepository
import com.lovelycatv.sakurachat.service.AgentService
import com.lovelycatv.sakurachat.service.ThirdPartyAccountService
import com.lovelycatv.sakurachat.service.UserService
import com.lovelycatv.sakurachat.types.ThirdPartyPlatform
import com.lovelycatv.sakurachat.utils.EncryptUtils
import com.lovelycatv.sakurachat.utils.SnowIdGenerator
import com.lovelycatv.sakurachat.utils.toJSONString
import com.lovelycatv.vertex.log.logger
import com.mikuac.shiro.core.Bot
import com.mikuac.shiro.core.BotPlugin
import com.mikuac.shiro.dto.event.message.GroupMessageEvent
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.springframework.stereotype.Component

@Component
class NapCatBotPlugin(
    private val napcatPrivateMessageRepository: NapCatPrivateMessageRepository,
    private val napCatGroupMessageRepository: NapCatGroupMessageRepository,
    private val snowIdGenerator: SnowIdGenerator,
    private val thirdPartyAccountService: ThirdPartyAccountService,
    private val agentService: AgentService,
    private val userService: UserService,
    private val sakuraChatMessageChannelDaemon: SakuraChatMessageChannelDaemon
) : BotPlugin() {
    private val logger = logger()

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override fun onPrivateMessage(bot: Bot, event: PrivateMessageEvent): Int {
        coroutineScope.launch(Dispatchers.IO) {
            napcatPrivateMessageRepository.save(
                NapCatPrivateMessageEntity(
                    id = snowIdGenerator.nextId(),
                    messageId = event.messageId,
                    botId = bot.selfId,
                    senderId = event.privateSender.userId,
                    senderNickname = event.privateSender.nickname,
                    message = event.arrayMsg.joinToString(),
                    createdTime = event.time
                )
            )

            logger.info("Private message sent to bot ${bot.selfId} has been received and saved, message: $event")

            // Make sure the bot has been registered to 3rd party account table
            thirdPartyAccountService.getOrAddAccount(
                ThirdPartyPlatform.OICQ,
                bot
            )

            // Make sure the message sender has been registered to 3rd party account table
            thirdPartyAccountService.getOrAddAccount(
                ThirdPartyPlatform.OICQ,
                event.privateSender
            )

            val relatedAgent = agentService.getAgentByThirdPartyAccount(
                ThirdPartyPlatform.OICQ,
                bot.selfId.toString()
            )

            if (relatedAgent == null) {
                logger.warn("Cannot find related agent for OICQ Bot Account: ${bot.selfId}")
            } else {
                val relatedUser = userService.getUserByThirdPartyAccount(
                    ThirdPartyPlatform.OICQ,
                    event.privateSender.userId.toString()
                )

                if (relatedUser == null) {
                    logger.warn("Cannot find related user for OICQ User Account: ${event.privateSender.userId}")
                    bot.sendPrivateMsg(event.privateSender.userId, "Your OICQ account ${event.privateSender.userId} has not been registered in SakuraChat, please turn to https://sakurachat.lovelycatv.com and bind your OICQ Account.", true)
                } else {
                    val agent = agentService.toAggregatedAgentEntity(relatedAgent)
                    logger.info("Agent ${agent.agent.name} found for handling this private message: ${event.message}")
                    logger.info("Agent: ${agent.copy(agent = agent.agent.copy(prompt = "<...>")).toJSONString()}")

                    // Find the private message channel
                    val privateMessageChannel = sakuraChatMessageChannelDaemon.getPrivateMessageChannel(
                        agent,
                        relatedUser
                    )

                    privateMessageChannel.sendPrivateMessage(
                        sender = privateMessageChannel.getUserMember(relatedUser.id!!)
                            ?: throw IllegalArgumentException("Member user: ${relatedUser.id} is not in channel ${privateMessageChannel.getChannelIdentifier()}"),
                        receiver = privateMessageChannel.getAgentMember(agent.agent.id!!)
                            ?: throw IllegalArgumentException("Member agent: ${agent.agent.id} is not in channel ${privateMessageChannel.getChannelIdentifier()}"),
                        message = TextMessage(
                             sequence = System.currentTimeMillis(),
                             message = event.message,
                             extraBody = SakuraChatMessageExtra(
                                 ThirdPartyPlatform.OICQ,
                                 event.privateSender.userId.toString(),
                                 bot
                             )
                        )
                    )
                }
            }
        }

        return super.onPrivateMessage(bot, event)
    }

    override fun onGroupMessage(bot: Bot, event: GroupMessageEvent): Int {
        val hash = EncryptUtils.calculateHash(event.rawMessage, "SHA-256")

        val existing = napCatGroupMessageRepository.getByHash(hash)

        if (existing != null) {
            logger.info("Found duplicated group message with hash $hash which has recorded by bot ${existing.botId}")
        } else {
            napCatGroupMessageRepository.save(
                NapCatGroupMessageEntity(
                    id = snowIdGenerator.nextId(),
                    messageId = event.messageId,
                    groupId = event.groupId,
                    botId = bot.selfId,
                    senderId = event.sender.userId,
                    senderNickname = event.sender.nickname,
                    message = event.arrayMsg.joinToString(),
                    createdTime = event.time,
                    hash = hash
                )
            )

            logger.info("Group message received and saved by bot ${bot.selfId}, message: $event")
        }

        return super.onGroupMessage(bot, event)
    }
}