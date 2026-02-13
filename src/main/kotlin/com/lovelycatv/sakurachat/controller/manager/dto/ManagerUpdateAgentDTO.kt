/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.controller.manager.dto

import jakarta.validation.constraints.Min

class ManagerUpdateAgentDTO(
    @field:Min(1, message = "Invalid resource id")
    val id: Long,
    val name: String?,
    val description: String?,
    val prompt: String?,
    val delimiter: String?,
    val userId: Long?,
    val chatModelId: Long?
)
