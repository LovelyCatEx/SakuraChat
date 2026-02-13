/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.controller.manager.dto

import jakarta.validation.constraints.Min

class ManagerUpdateUserDTO(
    @field:Min(1, message = "Invalid user id")
    val id: Long,
    val nickname: String?,
    val email: String?,
    val points: Long?
)
