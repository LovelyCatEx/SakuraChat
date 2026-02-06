/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.entity.aggregated

import com.lovelycatv.sakurachat.entity.AgentEntity

data class AggregatedAgentEntity(
    val agent: AgentEntity,
    val chatModel: AggregatedChatModelEntity?
)