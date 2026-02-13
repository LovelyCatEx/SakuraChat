/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.service.impl

import com.lovelycatv.sakurachat.controller.manager.dto.UpdateUserRoleRelationDTO
import com.lovelycatv.sakurachat.entity.UserRoleRelationEntity
import com.lovelycatv.sakurachat.repository.UserRoleRelationRepository
import com.lovelycatv.sakurachat.repository.UserRepository
import com.lovelycatv.sakurachat.request.PaginatedResponseData
import com.lovelycatv.sakurachat.service.UserRoleRelationService
import com.lovelycatv.sakurachat.utils.SnowIdGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserRoleRelationServiceImpl(
    private val userRoleRelationRepository: UserRoleRelationRepository,
    private val userRepository: UserRepository,
    private val snowIdGenerator: SnowIdGenerator
) : UserRoleRelationService {
    override fun getRepository(): UserRoleRelationRepository {
        return this.userRoleRelationRepository
    }

    @Transactional
    override fun updateUserRoleRelations(updateUserRoleRelationDTO: UpdateUserRoleRelationDTO) {
        val userId = updateUserRoleRelationDTO.userId
        val roleIds = updateUserRoleRelationDTO.roleIds

        userRoleRelationRepository.deleteAllByUserId(userId)

        roleIds.forEach {roleId ->
            userRoleRelationRepository.save(
                UserRoleRelationEntity(
                    id = snowIdGenerator.nextId(),
                    userId = userId,
                    roleId = roleId.toLong()
                )
            )
        }
    }

    override suspend fun getUserRolesByUserId(userId: Long): List<String> {
        return withContext(Dispatchers.IO) {
            userRoleRelationRepository.findAllByUserId(userId)
        }.map { it.roleId.toString() }
    }

    override suspend fun search(keyword: String, page: Int, pageSize: Int): PaginatedResponseData<Pair<Long, List<String>>> {
        val users = withContext(Dispatchers.IO) {
            if (keyword.isBlank()) {
                userRepository.findAll(Pageable.ofSize(pageSize).withPage(page - 1))
            } else {
                userRepository.findAllByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                    keyword,
                    keyword,
                    Pageable.ofSize(pageSize).withPage(page - 1)
                )
            }
        }

        val userRoleMap = withContext(Dispatchers.IO) {
            userRoleRelationRepository.findAllByUserIdIn(users.content.map { it.id!! })
        }.groupBy { it.userId }.mapValues { it.value.map { relation -> relation.roleId.toString() } }

        val data = users.content.map { user ->
            Pair(user.id, userRoleMap[user.id] ?: emptyList())
        }

        return PaginatedResponseData(
            records = data,
            total = users.totalElements,
            totalPages = users.totalPages,
            page = page,
            pageSize = pageSize
        )
    }
}
