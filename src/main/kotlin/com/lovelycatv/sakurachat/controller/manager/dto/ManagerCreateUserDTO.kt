/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.controller.manager.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

class ManagerCreateUserDTO(
    @field:NotBlank(message = "username cannot be empty")
    @field:Pattern(regexp = "^[a-zA-Z0-9_]+", message = "username can only contain numbers, letters and underscores")
    @field:Size(max = 64, message = "username length should not be greater than 64")
    val username: String,
    @field:NotBlank(message = "password cannot be empty")
    @field:Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9]).{8,}$", message = "password must be at least 8 characters and contain numbers and letters")
    @field:Size(max = 128, message = "password length should not be greater than 128")
    val password: String,
    @field:NotBlank(message = "nickname cannot be empty")
    @field:Size(max = 64, message = "nickname length should not be greater than 64")
    val nickname: String,
    @field:NotBlank(message = "email cannot be empty")
    @field:Email(message = "email format is invalid")
    @field:Size(max = 256, message = "email length should not be greater than 256")
    val email: String,
    @field:Min(value = 0, message = "points should not be less than 0")
    val points: Long = 0
)
