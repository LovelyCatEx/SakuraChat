/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.controller.manager.dto

import jakarta.validation.constraints.Min

class ManagerUpdateChatModelDTO(
    @field:Min(1, message = "Invalid resource id")
    val id: Long,
    val name: String?,
    val description: String?,
    val providerId: Long?,
    val qualifiedName: String?,
    val maxContextTokens: Int?,
    val temperature: Int?,
    val maxTokens: Int?,
    val inputTokenPointRate: Int?,
    val outputTokenPointRate: Int?,
    val cachedInputTokenPointRate: Int?,
    val credentialId: Long?,
    val active: Boolean?
)
