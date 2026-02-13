/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.service

import com.lovelycatv.sakurachat.controller.manager.dto.ManagerCreateUserRoleDTO
import com.lovelycatv.sakurachat.controller.manager.dto.UpdateUserRoleDTO
import com.lovelycatv.sakurachat.entity.UserRoleEntity
import com.lovelycatv.sakurachat.repository.UserRoleRepository
import com.lovelycatv.sakurachat.request.PaginatedResponseData
import com.lovelycatv.sakurachat.types.UserRoleType

interface UserRoleService : BaseService<UserRoleRepository, UserRoleEntity, Long> {
    suspend fun updateUserRole(updateUserRoleDTO: UpdateUserRoleDTO)

    suspend fun createUserRole(managerCreateUserRoleDTO: ManagerCreateUserRoleDTO)

    suspend fun search(keyword: String, page: Int, pageSize: Int): PaginatedResponseData<UserRoleEntity>

    fun getRoleEntityByType(type: UserRoleType): UserRoleEntity
}
