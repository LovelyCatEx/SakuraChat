/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.controller.manager.dto

import jakarta.validation.constraints.NotNull

class ManagerUpdateUserRoleRelationDTO(
    @field:NotNull
    var userId: Long,
    val roleIds: List<String>
)
