/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.service.impl

import com.lovelycatv.lark.LarkRestClient
import com.lovelycatv.lark.LarkWebSocketClient
import com.lovelycatv.sakurachat.entity.LarkBotApplicationEntity
import com.lovelycatv.sakurachat.repository.LarkBotApplicationRepository
import com.lovelycatv.sakurachat.service.LarkBotApplicationService
import org.springframework.stereotype.Service

@Service
class LarkBotApplicationServiceImpl(
    private val larkBotApplicationRepository: LarkBotApplicationRepository,
) : LarkBotApplicationService {
    override fun getRepository(): LarkBotApplicationRepository {
        return this.larkBotApplicationRepository
    }

    override fun getLarkRestClient(application: LarkBotApplicationEntity): LarkRestClient {
        return LarkRestClient(
            appId = application.appId,
            appSecret = application.appSecret
        )
    }

    override fun getLarkWebSocketClient(application: LarkBotApplicationEntity): LarkWebSocketClient {
        return LarkWebSocketClient(
            appId = application.appId,
            appSecret = application.appSecret,
            encryptKey = application.encryptKey,
            verificationToken = application.verificationToken
        )
    }
}