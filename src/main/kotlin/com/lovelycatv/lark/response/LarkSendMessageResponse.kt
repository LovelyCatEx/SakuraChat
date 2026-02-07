/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.lark.response

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class LarkSendMessageResponse @JsonCreator constructor(
    @field:JsonProperty("code")
    val code: Int,
    @field:JsonProperty("msg")
    val msg: String,
    @field:JsonProperty("data")
    val data: MessageData
) {
    data class MessageData @JsonCreator constructor(
        @field:JsonProperty("body")
        val body: MessageBody,
        @field:JsonProperty("chat_id")
        val chatId: String,
        @field:JsonProperty("create_time")
        val createTime: String,
        @field:JsonProperty("deleted")
        val deleted: Boolean,
        @field:JsonProperty("message_id")
        val messageId: String,
        @field:JsonProperty("msg_type")
        val msgType: String,
        @field:JsonProperty("sender")
        val sender: SenderInfo,
        @field:JsonProperty("update_time")
        val updateTime: String,
        @field:JsonProperty("updated")
        val updated: Boolean
    )

    data class MessageBody @JsonCreator constructor(
        @field:JsonProperty("content")
        val content: String
    )

    data class SenderInfo @JsonCreator constructor(
        @field:JsonProperty("id")
        val id: String,
        @field:JsonProperty("id_type")
        val idType: String,
        @field:JsonProperty("sender_type")
        val senderType: String,
        @field:JsonProperty("tenant_key")
        val tenantKey: String
    )
}