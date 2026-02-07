/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.lark

import com.lovelycatv.lark.message.LarkTextMessage
import com.lovelycatv.lark.type.LarkIdType
import com.lovelycatv.sakurachat.utils.toJSONString
import org.junit.jupiter.api.Test

class LarkRestClientTest {
    private val appId = System.getenv("LARK_APP_ID")
    private val appSecret = System.getenv("LARK_APP_SECRET")
    private val messageReceiverId = System.getenv("LARK_MESSAGE_RECEIVER_UNION_ID")

    private val client = LarkRestClient(appId, appSecret)

    @Test
    fun getTenantToken() {
        val token = client.getTenantToken().token

        println(token)
    }

    @Test
    fun sendMessage() {
        val resp = client.sendMessage(
            LarkIdType.UNION_ID,
            messageReceiverId,
            LarkTextMessage("Hello, World!"),
        )

        println(resp.toJSONString())
    }
}