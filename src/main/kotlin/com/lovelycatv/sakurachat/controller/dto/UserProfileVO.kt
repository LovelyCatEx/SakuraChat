/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.controller.dto

data class UserProfileVO(
    val userId: Long,
    val username: String,
    val nickname: String,
    val email: String?,
)
