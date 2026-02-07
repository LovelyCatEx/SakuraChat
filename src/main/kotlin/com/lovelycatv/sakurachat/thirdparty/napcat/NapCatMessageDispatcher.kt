/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.thirdparty.napcat

import com.lovelycatv.sakurachat.adapters.thirdparty.message.MessageAdapterManager
import com.lovelycatv.sakurachat.core.SakuraChatMessageExtra
import com.lovelycatv.sakurachat.daemon.SakuraChatMessageChannelDaemon
import com.lovelycatv.sakurachat.entity.napcat.NapCatGroupMessageEntity
import com.lovelycatv.sakurachat.entity.napcat.NapCatPrivateMessageEntity
import com.lovelycatv.sakurachat.repository.NapCatGroupMessageRepository
import com.lovelycatv.sakurachat.repository.NapCatPrivateMessageRepository
import com.lovelycatv.sakurachat.service.AgentService
import com.lovelycatv.sakurachat.service.ThirdPartyAccountService
import com.lovelycatv.sakurachat.service.UserService
import com.lovelycatv.sakurachat.thirdparty.AbstractThirdPartyMessageDispatcher
import com.lovelycatv.sakurachat.types.ThirdPartyPlatform
import com.lovelycatv.sakurachat.utils.EncryptUtils
import com.lovelycatv.sakurachat.utils.SnowIdGenerator
import com.lovelycatv.sakurachat.utils.toJSONString
import com.lovelycatv.vertex.log.logger
import com.mikuac.shiro.core.Bot
import com.mikuac.shiro.dto.event.message.GroupMessageEvent
import com.mikuac.shiro.dto.event.message.MessageEvent
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Component

@Component
class NapCatMessageDispatcher(
    private val napcatPrivateMessageRepository: NapCatPrivateMessageRepository,
    private val napCatGroupMessageRepository: NapCatGroupMessageRepository,
    private val snowIdGenerator: SnowIdGenerator,
    private val thirdPartyAccountService: ThirdPartyAccountService,
    private val agentService: AgentService,
    private val userService: UserService,
    private val sakuraChatMessageChannelDaemon: SakuraChatMessageChannelDaemon,
    private val messageAdapterManager: MessageAdapterManager
) : AbstractThirdPartyMessageDispatcher(ThirdPartyPlatform.NAPCAT_OICQ) {
    private val logger = logger()

    override suspend fun doHandle(vararg args: Any?): Boolean {
        return this.handle(
            bot = firstInstanceOrNull<Bot>(*args),
            event = firstInstanceOrNull<MessageEvent>(*args)
        )
    }

    private suspend fun handle(bot: Bot?, event: MessageEvent?): Boolean {
        if (bot == null || event == null) {
            logger.warn("Could not handle message from napcat caused by one of bot and event is null, Bot ${bot?.selfId} received message from NapCat from event: ${event?.toJSONString()}")
            return false
        }

        // 1. Save napcat logs to database
        withContext(Dispatchers.IO) {
            // Private Message
            if (event is PrivateMessageEvent) {
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
            } else if (event is GroupMessageEvent) {
                // Group Message
                val hash = EncryptUtils.calculateHash(event.rawMessage, "SHA-256")

                val existing = withContext(Dispatchers.IO) {
                    napCatGroupMessageRepository.getByHash(hash)
                }

                if (existing != null) {
                    logger.info("Found duplicated group message with hash $hash which has recorded by bot ${existing.botId}")
                } else {
                    withContext(Dispatchers.IO) {
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
                    }

                    logger.info("Group message received and saved by bot ${bot.selfId}, message: $event")
                }
            } else {
                // Unsupported event type
                logger.warn("Could not save napcat logs caused by event ${event::class.qualifiedName} is not supported by SakuraChat.")
            }
        }

        // 2. Make sure the bot has been registered to 3rd party account table
        thirdPartyAccountService.getOrAddAccount(
            ThirdPartyPlatform.NAPCAT_OICQ,
            bot
        )

        // 3. Make sure the message sender has been registered to 3rd party account table
        if (event is PrivateMessageEvent) {
            thirdPartyAccountService.getOrAddAccount(
                ThirdPartyPlatform.NAPCAT_OICQ,
                event.privateSender
            )
        } else if (event is GroupMessageEvent) {
            thirdPartyAccountService.getOrAddAccount(
                ThirdPartyPlatform.NAPCAT_OICQ,
                event.sender
            )
        } else {
            logger.warn("Could not get or create third party account for event ${event::class.qualifiedName}")
            return false
        }

        // 4. Find the related agent
        val relatedAgent = agentService.getAgentByThirdPartyAccount(
            ThirdPartyPlatform.NAPCAT_OICQ,
            bot.selfId.toString()
        )

        if (relatedAgent == null) {
            logger.warn("Cannot find related agent for OICQ Bot Account: ${bot.selfId}")
            return false
        }

        // 5. Find the related user
        val userOICQId = if (event is PrivateMessageEvent) {
            event.privateSender.userId
        } else if (event is GroupMessageEvent) {
            event.sender.userId
        } else {
            logger.warn("Could not get OICQ userId from event: ${event::class.qualifiedName}")
            return false
        }

        val userPlatformAccountId = thirdPartyAccountService.getAccountIdByPlatformAccountObject(
            ThirdPartyPlatform.NAPCAT_OICQ,
            userOICQId
        )

        val relatedUser = userService.getUserByThirdPartyAccount(
            ThirdPartyPlatform.NAPCAT_OICQ,
            thirdPartyAccountService.getAccountIdByPlatformAccountObject(
                ThirdPartyPlatform.NAPCAT_OICQ,
                userPlatformAccountId
            )
        )

        if (relatedUser == null) {
            logger.warn("Cannot find related user for OICQ User Account: $userOICQId")
            bot.sendPrivateMsg(
                userOICQId,
                "Your OICQ account $userOICQId has not been registered in SakuraChat, please turn to https://sakurachat.lovelycatv.com and bind your OICQ Account.",
                true
            )

            return false
        }

        val agent = agentService.toAggregatedAgentEntity(relatedAgent)

        logger.info("Agent ${agent.agent.name} found for handling this private message: ${event.message}")
        logger.info("Agent: ${agent.copy(agent = agent.agent.copy(prompt = "<...>")).toJSONString()}")

        // 6. Find the private message channel
        val privateMessageChannel = sakuraChatMessageChannelDaemon.getPrivateMessageChannel(
            agent,
            relatedUser
        )

        // 7. Prepare message
        val messageToSend = messageAdapterManager
            .getAdapter(ThirdPartyPlatform.NAPCAT_OICQ)
            ?.transform(
                input = event,
                extraBody = SakuraChatMessageExtra(
                    ThirdPartyPlatform.NAPCAT_OICQ,
                    userPlatformAccountId,
                    bot
                )
            )

        if (messageToSend == null) {
            logger.warn("Could not transform NapCat message to SakuraChat capable message")
            return false
        }

        // 8. Send message through the message channel
        return privateMessageChannel.sendPrivateMessage(
            sender = privateMessageChannel.getUserMember(relatedUser.id!!)
                ?: throw IllegalArgumentException("Member user: ${relatedUser.id} is not in channel ${privateMessageChannel.getChannelIdentifier()}"),
            receiver = privateMessageChannel.getAgentMember(agent.agent.id!!)
                ?: throw IllegalArgumentException("Member agent: ${agent.agent.id} is not in channel ${privateMessageChannel.getChannelIdentifier()}"),
            message = messageToSend
        )
    }
}