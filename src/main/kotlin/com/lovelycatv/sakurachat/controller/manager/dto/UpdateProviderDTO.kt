/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.controller.manager.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotEmpty

data class UpdateProviderDTO(
    @field:Min(1, message = "Invalid resource id")
    val id: Long,
    val name: String?,
    val description: String?,
    @field:Min(value = 0, message = "ApiType must greater than or equal to 0")
    val apiType: Int?,
    @field:NotEmpty(message = "chat completions url could not be empty")
    val chatCompletionsUrl: String?
)
