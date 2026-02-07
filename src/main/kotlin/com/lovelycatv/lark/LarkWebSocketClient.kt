/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.lark

import com.lark.oapi.event.EventDispatcher
import com.lark.oapi.ws.Client

class LarkWebSocketClient(
    private val appId: String,
    private val appSecret: String,
    encryptKey: String,
    verificationToken: String
) {
    private var isStarted = false
    private var isBuilt = false

    private lateinit var client: Client

    private val eventDispatcherBuilder = EventDispatcher.newBuilder(
        verificationToken,
        encryptKey
    )

    fun eventDispatcher(block: EventDispatcher.Builder.() -> Unit) {
        block.invoke(eventDispatcherBuilder)
    }

    fun build() {
        if (!isBuilt) {
            this.client = Client.Builder(appId, appSecret)
                .eventHandler(
                    eventDispatcherBuilder.build()
                ).build()
            isBuilt = true
        }
    }

    fun start() {
        if (!isBuilt) {
            throw IllegalStateException("Lark WebSocket client is not built yet.")
        }

        if (!isStarted) {
            this.client.start()
            isStarted = true
        }
    }
}