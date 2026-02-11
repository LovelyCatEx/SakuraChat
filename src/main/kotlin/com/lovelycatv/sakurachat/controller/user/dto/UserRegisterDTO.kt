/*
 * Copyright 2025 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */
package com.lovelycatv.sakurachat.controller.user.dto

data class UserRegisterDTO(
    val username: String,
    val password: String,
    val email: String,
    val emailCode: String
)