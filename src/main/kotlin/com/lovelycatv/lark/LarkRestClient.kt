/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.lark

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.lark.oapi.Client
import com.lark.oapi.core.response.BaseResponse
import com.lark.oapi.service.auth.v3.model.InternalTenantAccessTokenReq
import com.lark.oapi.service.auth.v3.model.InternalTenantAccessTokenReqBody
import com.lark.oapi.service.im.v1.model.CreateMessageReq
import com.lark.oapi.service.im.v1.model.CreateMessageReqBody
import com.lovelycatv.lark.exception.LarkRestRequestException
import com.lovelycatv.lark.message.AbstractLarkMessage
import com.lovelycatv.lark.message.LarkTextMessage
import com.lovelycatv.lark.response.LarkGetTenantTokenResponse
import com.lovelycatv.lark.response.LarkSendMessageResponse
import com.lovelycatv.lark.type.LarkIdType
import java.util.*

class LarkRestClient(
    val appId: String,
    val appSecret: String
) {
    private val objectMapper = jacksonObjectMapper().apply {
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    private val client = Client.newBuilder(appId, appSecret).build()

    /**
     * https://open.feishu.cn/document/server-docs/authentication-management/access-token/tenant_access_token_internal
     *
     * @return [LarkGetTenantTokenResponse]
     */
    fun getTenantToken(): LarkGetTenantTokenResponse {
        val req = InternalTenantAccessTokenReq.newBuilder()
            .internalTenantAccessTokenReqBody(
                InternalTenantAccessTokenReqBody.newBuilder()
                .appId(appId)
                .appSecret(appSecret)
                .build()
            )
            .build()

        return handleBaseResponse(
            client.auth().v3().tenantAccessToken().internal(req),
            LarkGetTenantTokenResponse::class.java
        )
    }

    fun sendMessage(
        receiverIdType: LarkIdType,
        receiverId: String,
        message: String
    ): LarkSendMessageResponse {
        return this.sendMessage(receiverIdType, receiverId, LarkTextMessage(message))
    }

    /**
     * https://open.feishu.cn/document/server-docs/im-v1/message/create
     *
     * @param receiverIdType Only supports: [LarkIdType.USER_ID], [LarkIdType.OPEN_ID], [LarkIdType.UNION_ID], [LarkIdType.CHAT_ID]
     * @param receiverId Corresponding receiver id depends by [receiverIdType]
     * @param message [AbstractLarkMessage]
     * @return [LarkSendMessageResponse]
     */
    fun sendMessage(
        receiverIdType: LarkIdType,
        receiverId: String,
        message: AbstractLarkMessage
    ): LarkSendMessageResponse {
        val validTypes = arrayOf(
            LarkIdType.USER_ID,
            LarkIdType.CHAT_ID,
            LarkIdType.OPEN_ID,
            LarkIdType.UNION_ID
        )

        if (receiverIdType !in validTypes) {
            throw IllegalArgumentException("Receiver Id Type is invalid: $receiverIdType, supports: ${validTypes.joinToString()}")
        }

        val req = CreateMessageReq.newBuilder()
            .receiveIdType(receiverIdType.typeName)
            .createMessageReqBody(
                CreateMessageReqBody.newBuilder()
                    .receiveId(receiverId)
                    .msgType(message.type.typeName)
                    .content(objectMapper.writeValueAsString(message))
                    .uuid(UUID.randomUUID().toString())
                    .build()
            )
            .build()

        return handleBaseResponse(
            client.im().v1().message().create(req),
            LarkSendMessageResponse::class.java
        )
    }

    private fun <T, R: BaseResponse<*>> handleBaseResponse(resp: R?, clazz: Class<T>): T {
        if (resp == null || resp.rawResponse == null) {
            throw LarkRestRequestException("Response body is null")
        }

        if (resp.success()) {
            return objectMapper.readValue(resp.rawResponse.body, clazz)
        } else {
            throw LarkRestRequestException(
                "Lark rest api request failed, " +
                        "code: ${resp.code}, " +
                        "message: ${resp.msg}, " +
                        "requestId: ${resp.requestId}, " +
                        "body: ${String(resp.rawResponse.body)}"
            )
        }
    }
}