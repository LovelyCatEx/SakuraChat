/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.service.impl

import com.lovelycatv.sakurachat.controller.manager.dto.UpdateUserRoleDTO
import com.lovelycatv.sakurachat.entity.UserRole
import com.lovelycatv.sakurachat.repository.UserRoleRepository
import com.lovelycatv.sakurachat.service.UserRoleService
import org.springframework.stereotype.Service

@Service
class UserRoleServiceImpl(
    private val userRoleRepository: UserRoleRepository,
) : UserRoleService {
    override fun getRepository(): UserRoleRepository {
        return this.userRoleRepository
    }

    override suspend fun updateUserRole(updateUserRoleDTO: UpdateUserRoleDTO) {
        val existing = this.getByIdOrThrow(updateUserRoleDTO.id)

        if (updateUserRoleDTO.name != null) {
            existing.name = updateUserRoleDTO.name
        }

        if (updateUserRoleDTO.description != null) {
            existing.description = updateUserRoleDTO.description
        }

        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            getRepository().save(existing)
        }
    }
}
