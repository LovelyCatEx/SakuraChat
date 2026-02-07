/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.thirdparty.lark

import com.lark.oapi.service.im.v1.model.P2MessageReadV1
import com.lark.oapi.service.im.v1.model.P2MessageReceiveV1
import com.lovelycatv.lark.LarkRestClient
import com.lovelycatv.lark.type.LarkChatMessageType
import com.lovelycatv.lark.type.LarkIdType
import com.lovelycatv.sakurachat.adapters.thirdparty.message.MessageAdapterManager
import com.lovelycatv.sakurachat.core.SakuraChatMessageExtra
import com.lovelycatv.sakurachat.daemon.SakuraChatMessageChannelDaemon
import com.lovelycatv.sakurachat.service.AgentService
import com.lovelycatv.sakurachat.service.ThirdPartyAccountService
import com.lovelycatv.sakurachat.service.UserService
import com.lovelycatv.sakurachat.thirdparty.AbstractThirdPartyMessageDispatcher
import com.lovelycatv.sakurachat.types.ThirdPartyPlatform
import com.lovelycatv.sakurachat.utils.SnowIdGenerator
import com.lovelycatv.sakurachat.utils.toJSONString
import com.lovelycatv.vertex.log.logger
import org.springframework.stereotype.Component

@Component
class LarkMessageDispatcher(
    private val snowIdGenerator: SnowIdGenerator,
    private val thirdPartyAccountService: ThirdPartyAccountService,
    private val agentService: AgentService,
    private val userService: UserService,
    private val sakuraChatMessageChannelDaemon: SakuraChatMessageChannelDaemon,
    private val messageAdapterManager: MessageAdapterManager
) : AbstractThirdPartyMessageDispatcher(ThirdPartyPlatform.LARK) {
    private val logger = logger()

    override suspend fun doHandle(vararg args: Any?): Boolean {
        val larkRestClient = firstInstanceOrNull<LarkRestClient>(*args)
            ?: throw IllegalArgumentException("Could not dispatch unsupported event received from lark " +
                    "caused by ${LarkRestClient::class.qualifiedName} not found in args. " +
                    "Received args: ${args.joinToString()}"
            )

        return firstInstanceOrNull<P2MessageReceiveV1>(*args)?.let {
            handleMessageReceivedEvent(larkRestClient, it)
        } ?: firstInstanceOrNull<P2MessageReadV1>(*args)?.let {
            handleMessageReadEvent(larkRestClient, it)
        } ?: throw IllegalArgumentException("Could not dispatch unsupported event received from lark," +
                " args: ${args.joinToString()}"
        )
    }

    private suspend fun handleMessageReceivedEvent(client: LarkRestClient, event: P2MessageReceiveV1): Boolean {
        // 2. Make sure the bot has been registered to 3rd party account table
        thirdPartyAccountService.getOrAddAccount(
            ThirdPartyPlatform.LARK,
            client
        )

        // 3. Make sure the message sender has been registered to 3rd party account table
        thirdPartyAccountService.getOrAddAccount(
            ThirdPartyPlatform.LARK,
            event.event.sender
        )

        // 4. Find the related agent
        val relatedAgentAccountId = thirdPartyAccountService.getAccountIdByPlatformAccountObject(
            ThirdPartyPlatform.LARK,
            client
        )

        val relatedAgent = agentService.getAgentByThirdPartyAccount(
            ThirdPartyPlatform.LARK,
            relatedAgentAccountId
        )

        if (relatedAgent == null) {
            logger.warn("Cannot find related agent for Lark Bot Account: $relatedAgentAccountId")
            return false
        }

        // 5. Find the related user
        val userPlatformAccountId = event.event.sender
        val userAccountId = thirdPartyAccountService.getAccountIdByPlatformAccountObject(
            ThirdPartyPlatform.LARK,
            userPlatformAccountId
        )

        val relatedUser = userService.getUserByThirdPartyAccount(
            ThirdPartyPlatform.LARK,
            userAccountId
        )

        if (relatedUser == null) {
            logger.warn("Cannot find related user for Lark User Account: ${userPlatformAccountId.senderId.toJSONString()}")

            client.sendMessage(
                LarkIdType.UNION_ID,
                userPlatformAccountId.senderId.unionId,
                "Your Lark account ${userPlatformAccountId.senderId.unionId} has not been registered in SakuraChat, please turn to https://sakurachat.lovelycatv.com and bind your Lark Account."
            )

            return false
        }

        // 6. Find the message channel
        val agent = agentService.toAggregatedAgentEntity(relatedAgent)

        logger.info("Agent ${agent.agent.name} found for handling this message: ${event.event.message.toJSONString()}")
        logger.info("Agent: ${agent.copy(agent = agent.agent.copy(prompt = "<...>")).toJSONString()}")

        val messageType = LarkChatMessageType.getByTypeName(event.event.message.chatType)

        val messageChannel = when (messageType) {
            LarkChatMessageType.P2P -> {
                sakuraChatMessageChannelDaemon.getPrivateMessageChannel(
                    agent,
                    relatedUser
                )
            }

            LarkChatMessageType.GROUP -> {
                sakuraChatMessageChannelDaemon.getGroupMessageChannel(
                    groupIdentifier = sakuraChatMessageChannelDaemon.buildGroupChannelIdentifier(
                        ThirdPartyPlatform.LARK,
                        event.event.message.chatId.toString()
                    ),
                    agent = agent,
                    user = relatedUser
                )
            }

            else -> {
                logger.warn("Could not get message channel for platform ${platform.name} event ${event::class.qualifiedName}")

                client.sendMessage(
                    LarkIdType.UNION_ID,
                    userPlatformAccountId.senderId.unionId,
                    "Could not get message channel for platform ${platform.name} event ${event::class.qualifiedName}"
                )

                return false
            }
        }

        // 7. Prepare message
        val messageToSend = messageAdapterManager
            .getAdapter(ThirdPartyPlatform.LARK, event.event.message::class.java)
            ?.transform(
                input = event.event.message,
                extraBody = SakuraChatMessageExtra(
                    platform = ThirdPartyPlatform.LARK,
                    platformAccountId = userPlatformAccountId.senderId.unionId,
                    platformInvoker = client
                )
            )

        if (messageToSend == null) {
            logger.warn("Could not transform Lark message to SakuraChat capable message")

            client.sendMessage(
                LarkIdType.UNION_ID,
                event.event.sender.senderId.unionId,
                "Could not transform Lark message to SakuraChat capable message"
            )

            return false
        }

        // 8. Send message through the message channel
        return when (messageType) {
            LarkChatMessageType.P2P -> {
                messageChannel.sendPrivateMessage(
                    sender = messageChannel.getUserMember(relatedUser.id!!)
                        ?: throw IllegalArgumentException("Member user: ${relatedUser.id} is not in channel ${messageChannel.getChannelIdentifier()}"),
                    receiver = messageChannel.getAgentMember(agent.agent.id!!)
                        ?: throw IllegalArgumentException("Member agent: ${agent.agent.id} is not in channel ${messageChannel.getChannelIdentifier()}"),
                    message = messageToSend
                )
            }

            LarkChatMessageType.GROUP -> {
                messageChannel.sendGroupMessage(
                    sender = messageChannel.getUserMember(relatedUser.id!!)
                        ?: throw IllegalArgumentException("Member user: ${relatedUser.id} is not in channel ${messageChannel.getChannelIdentifier()}"),
                    message = messageToSend
                )
            }

            else -> {
                logger.warn("Unsupported lark message type $messageType")
                false
            }
        }
    }

    private suspend fun handleMessageReadEvent(client: LarkRestClient, event: P2MessageReadV1): Boolean {
        return true
    }
}