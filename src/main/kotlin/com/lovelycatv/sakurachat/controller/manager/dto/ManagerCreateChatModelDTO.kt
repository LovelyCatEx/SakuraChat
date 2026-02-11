/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.controller.manager.dto

import com.lovelycatv.sakurachat.annotations.ResourceId
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

class ManagerCreateChatModelDTO(
    @field:NotBlank(message = "model name cannot be empty")
    val name: String,
    val description: String?,
    @field:ResourceId
    val providerId: Long,
    @field:ResourceId
    val credentialId: Long,
    @field:NotBlank(message = "model qualified name cannot be empty")
    val qualifiedName: String,
    @field:Min(-1, message = "max context tokens should not be less than -1")
    val maxContextTokens: Int,
    @field:Min(-1, message = "temperature should not be less than -1")
    @field:Max(200, message = "temperature should not be greater than 200")
    val temperature: Int,
    @field:Min(-1, message = "max tokens should not be less than -1")
    val maxTokens: Int,
    @field:Min(0, message = "input tokens point rate should not be less than 0")
    val inputTokenPointRate: Int,
    @field:Min(0, message = "output tokens point rate should not be less than 0")
    val outputTokenPointRate: Int,
    @field:Min(0, message = "cached input tokens point rate should not be less than 0")
    val cachedInputTokenPointRate: Int,
)