/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.controller.manager.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

class ManagerCreateProviderDTO(
    @field:NotBlank(message = "provider name cannot be empty")
    @field:Size(max = 32, message = "provider name length should not be greater than 32")
    val name: String,
    @field:Size(max = 512, message = "description length should not be greater than 512")
    val description: String?,
    @field:NotBlank(message = "chat completions url cannot be empty")
    val chatCompletionsUrl: String,
    @field:NotNull(message = "api type cannot be null")
    val apiType: Int
)
