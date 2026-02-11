/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.service

import com.lovelycatv.sakurachat.controller.manager.dto.UpdateUserRoleDTO
import com.lovelycatv.sakurachat.entity.UserRole
import com.lovelycatv.sakurachat.repository.UserRoleRepository

interface UserRoleService : BaseService<UserRoleRepository, UserRole, Long> {
    suspend fun updateUserRole(updateUserRoleDTO: UpdateUserRoleDTO)
}
