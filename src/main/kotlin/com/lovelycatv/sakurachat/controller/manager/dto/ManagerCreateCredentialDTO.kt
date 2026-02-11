/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.controller.manager.dto

import jakarta.validation.constraints.NotBlank

class ManagerCreateCredentialDTO(
    val type: Int,
    @field:NotBlank(message = "credential data cannot be empty")
    val data: String,
    val active: Boolean = true
)
