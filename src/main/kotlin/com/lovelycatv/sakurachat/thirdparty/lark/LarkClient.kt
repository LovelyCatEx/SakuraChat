/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.thirdparty.lark

import com.fasterxml.jackson.databind.ObjectMapper
import com.lark.oapi.event.EventDispatcher
import com.lark.oapi.service.im.ImService
import com.lark.oapi.service.im.v1.model.P2MessageReadV1
import com.lark.oapi.service.im.v1.model.P2MessageReceiveV1
import com.lark.oapi.ws.Client
import com.lovelycatv.sakurachat.thirdparty.ThirdPartyMessageDispatcherManager
import com.lovelycatv.sakurachat.types.ThirdPartyPlatform
import com.lovelycatv.sakurachat.utils.toJSONString
import com.lovelycatv.vertex.log.logger
import org.springframework.beans.factory.InitializingBean
import org.springframework.stereotype.Component

@Component
class LarkClient(
    private val objectMapper: ObjectMapper,
    private val thirdPartyMessageDispatcherManager: ThirdPartyMessageDispatcherManager
) : InitializingBean {
    private val logger = logger()

    override fun afterPropertiesSet() {
        val cli: Client = Client.Builder("cli_a90cfd194b785bca", "V2biMPYm3J3nMuYmi9LgSb8J5d2v0iFQ")
            .eventHandler(
                EventDispatcher.newBuilder(
                    "e41Fr44jlNupzQfzQQgrUfFsZewTj3YY",
                    "VA2VWNWgkUXygyKqHO3sbdRKJvuYmBA7"
                ).onP2MessageReceiveV1(object : ImService.P2MessageReceiveV1Handler() {
                    override fun handle(p0: P2MessageReceiveV1) {
                        thirdPartyMessageDispatcherManager.dispatch(ThirdPartyPlatform.LARK, p0)
                    }

                }).onP2MessageReadV1(object : ImService.P2MessageReadV1Handler() {
                    override fun handle(p0: P2MessageReadV1) {
                        println(p0.toJSONString())
                    }

                }).build()
            )
            .build()
        cli.start()
    }
}