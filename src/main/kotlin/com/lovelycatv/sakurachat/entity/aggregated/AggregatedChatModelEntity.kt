/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.entity.aggregated

import com.lovelycatv.sakurachat.entity.ChatModelEntity
import com.lovelycatv.sakurachat.entity.CredentialEntity
import com.lovelycatv.sakurachat.entity.ProviderEntity

data class AggregatedChatModelEntity(
    val chatModel: ChatModelEntity,
    val provider: ProviderEntity?,
    val credential: CredentialEntity?
)
