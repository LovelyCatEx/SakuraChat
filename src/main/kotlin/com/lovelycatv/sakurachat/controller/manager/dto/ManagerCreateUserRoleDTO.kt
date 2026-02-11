/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.controller.manager.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

class ManagerCreateUserRoleDTO(
    @field:NotBlank(message = "role name cannot be empty")
    @field:Size(max = 32, message = "role name length should not be greater than 32")
    val name: String,
    @field:Size(max = 256, message = "description length should not be greater than 256")
    val description: String?
)
