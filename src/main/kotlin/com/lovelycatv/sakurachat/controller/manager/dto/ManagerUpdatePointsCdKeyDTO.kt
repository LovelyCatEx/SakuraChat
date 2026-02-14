/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.controller.manager.dto

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

class ManagerUpdatePointsCdKeyDTO(
    @field:NotNull(message = "id cannot be null")
    var id: Long,
    @field:Size(max = 64, message = "cdkey length should not be greater than 64")
    val cdKey: String? = null,
    val points: Long? = null,
    val generatedBy: Long? = null,
    val usedBy: Long? = null
)
