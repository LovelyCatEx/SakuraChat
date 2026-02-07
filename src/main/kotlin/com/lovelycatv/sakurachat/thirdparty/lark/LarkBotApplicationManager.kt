/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.thirdparty.lark

import com.lark.oapi.service.im.ImService
import com.lark.oapi.service.im.v1.model.P2MessageReadV1
import com.lark.oapi.service.im.v1.model.P2MessageReceiveV1
import com.lovelycatv.lark.LarkRestClient
import com.lovelycatv.lark.LarkWebSocketClient
import com.lovelycatv.sakurachat.entity.LarkBotApplicationEntity
import com.lovelycatv.sakurachat.service.LarkBotApplicationService
import com.lovelycatv.sakurachat.thirdparty.ThirdPartyMessageDispatcherManager
import com.lovelycatv.sakurachat.types.ThirdPartyPlatform
import com.lovelycatv.sakurachat.utils.toJSONString
import com.lovelycatv.vertex.log.logger
import org.springframework.beans.factory.InitializingBean
import org.springframework.stereotype.Component

@Component
class LarkBotApplicationManager(
    private val thirdPartyMessageDispatcherManager: ThirdPartyMessageDispatcherManager,
    private val larkBotApplicationService: LarkBotApplicationService
) : InitializingBean {
    private val logger = logger()

    private val runningApplications = mutableMapOf<Long, LarkClients>()
    private val stoppedApplications = mutableMapOf<Long, LarkClients>()


    override fun afterPropertiesSet() {
        hotRefresh()
    }

    fun hotRefresh() {
        logger.info("=".repeat(64))

        // 1. Find out all lark bot applications
        val applications = larkBotApplicationService
            .getRepository()
            .findAll()
            .filter { it.id !in runningApplications.keys }
            .filter { it.id !in stoppedApplications.keys }

        // 2. Build lark clients
        val clients = applications.map {
            LarkClients(
                application = it,
                restClient = larkBotApplicationService.getLarkRestClient(it),
                webSocketClient = larkBotApplicationService.getLarkWebSocketClient(it)
            )
        }

        // 3. Start websocket clients
        clients.forEach { client ->
            this.startWebSocketClient(client)
        }

        logger.info("=".repeat(64))
    }

    private fun startWebSocketClient(clients: LarkClients) {
        clients.webSocketClient.eventDispatcher {
            onP2MessageReceiveV1(object : ImService.P2MessageReceiveV1Handler() {
                override fun handle(p0: P2MessageReceiveV1) {
                    thirdPartyMessageDispatcherManager.dispatch(ThirdPartyPlatform.LARK, clients.restClient, p0)
                }
            })

            onP2MessageReadV1(object : ImService.P2MessageReadV1Handler() {
                override fun handle(p0: P2MessageReadV1) {
                    thirdPartyMessageDispatcherManager.dispatch(ThirdPartyPlatform.LARK, clients.restClient, p0)
                }
            })
        }

        clients.webSocketClient.build()

        try {
            clients.webSocketClient.start()
            stoppedApplications.remove(clients.application.id)
            runningApplications[clients.application.id] = clients

            logger.info("√ Lark application: ${clients.application.name} created by user ${clients.application.ownerUserId} started.")
        } catch (e: Exception) {
            logger.error("× Could not start lark web socket client, application: ${clients.application.toJSONString()}", e)
            runningApplications.remove(clients.application.id)
            stoppedApplications[clients.application.id] = clients
        }
    }

    private data class LarkClients(
        val application: LarkBotApplicationEntity,
        val restClient: LarkRestClient,
        val webSocketClient: LarkWebSocketClient
    )
}