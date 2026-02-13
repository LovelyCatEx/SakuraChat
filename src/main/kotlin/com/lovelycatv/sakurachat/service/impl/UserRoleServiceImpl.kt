/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.service.impl

import com.lovelycatv.sakurachat.controller.manager.dto.ManagerCreateUserRoleDTO
import com.lovelycatv.sakurachat.controller.manager.dto.ManagerUpdateUserRoleDTO
import com.lovelycatv.sakurachat.entity.UserRoleEntity
import com.lovelycatv.sakurachat.exception.BusinessException
import com.lovelycatv.sakurachat.repository.UserRoleRepository
import com.lovelycatv.sakurachat.request.PaginatedResponseData
import com.lovelycatv.sakurachat.service.UserRoleService
import com.lovelycatv.sakurachat.types.UserRoleType
import com.lovelycatv.sakurachat.utils.SnowIdGenerator
import com.lovelycatv.sakurachat.utils.toPaginatedResponseData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class UserRoleServiceImpl(
    private val userRoleRepository: UserRoleRepository,
    private val snowIdGenerator: SnowIdGenerator
) : UserRoleService {
    override fun getRepository(): UserRoleRepository {
        return this.userRoleRepository
    }

    override suspend fun updateUserRole(managerUpdateUserRoleDTO: ManagerUpdateUserRoleDTO) {
        val existing = this.getByIdOrThrow(managerUpdateUserRoleDTO.id)

        if (managerUpdateUserRoleDTO.name != null) {
            existing.name = managerUpdateUserRoleDTO.name
        }

        if (managerUpdateUserRoleDTO.description != null) {
            existing.description = managerUpdateUserRoleDTO.description
        }

        withContext(Dispatchers.IO) {
            getRepository().save(existing)
        }
    }

    override suspend fun createUserRole(managerCreateUserRoleDTO: ManagerCreateUserRoleDTO) {
        withContext(Dispatchers.IO) {
            getRepository().save(
                UserRoleEntity(
                    id = snowIdGenerator.nextId(),
                    name = managerCreateUserRoleDTO.name,
                    description = managerCreateUserRoleDTO.description
                )
            )
        }
    }

    override suspend fun search(keyword: String, page: Int, pageSize: Int): PaginatedResponseData<UserRoleEntity> {
        if (keyword.isBlank()) {
            return this.listByPage(page, pageSize).toPaginatedResponseData()
        }

        return withContext(Dispatchers.IO) {
            getRepository().findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                keyword,
                keyword,
                Pageable.ofSize(pageSize).withPage(page - 1)
            )
        }.toPaginatedResponseData()
    }

    override fun getRoleEntityByType(type: UserRoleType): UserRoleEntity {
        return this.getRepository().findAll().firstOrNull { it.name == type.roleName }
            ?: throw BusinessException("Could not find user role with name ${type.name}")
    }
}
