/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.controller.manager.dto

import com.lovelycatv.sakurachat.annotations.ResourceId
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

class ManagerCreateAgentDTO(
    @field:NotBlank(message = "agent name cannot be empty")
    @field:Size(max = 32, message = "agent name length should not be greater than 32")
    val name: String,
    @field:Size(max = 512, message = "description length should not be greater than 512")
    val description: String?,
    @field:NotBlank(message = "prompt cannot be empty")
    val prompt: String,
    @field:Size(max = 16, message = "delimiter length should not be greater than 16")
    val delimiter: String?,
    @field:ResourceId
    @field:Min(value = 1, message = "user id should not be less than 1")
    val userId: Long,
    @field:ResourceId
    @field:Min(value = 1, message = "chat model id should not be less than 1")
    val chatModelId: Long
)
