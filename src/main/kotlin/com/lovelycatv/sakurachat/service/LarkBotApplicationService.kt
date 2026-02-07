/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.service

import com.lovelycatv.lark.LarkRestClient
import com.lovelycatv.lark.LarkWebSocketClient
import com.lovelycatv.sakurachat.entity.LarkBotApplicationEntity
import com.lovelycatv.sakurachat.repository.LarkBotApplicationRepository

interface LarkBotApplicationService : BaseService<LarkBotApplicationRepository> {
    fun getLarkRestClient(application: LarkBotApplicationEntity): LarkRestClient

    fun getLarkWebSocketClient(application: LarkBotApplicationEntity): LarkWebSocketClient
}