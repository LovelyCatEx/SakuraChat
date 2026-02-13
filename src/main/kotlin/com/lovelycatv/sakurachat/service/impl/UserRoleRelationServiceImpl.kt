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
import com.lovelycatv.sakurachat.service.UserRoleService
import com.lovelycatv.sakurachat.types.UserRoleType
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
    private val snowIdGenerator: SnowIdGenerator,
    private val userRoleService: UserRoleService
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

    override fun getUserRolesByUserId(userId: Long): List<String> {
        return userRoleRelationRepository.findAllByUserId(userId).map { it.roleId.toString() }
    }

    override fun search(keyword: String, page: Int, pageSize: Int): PaginatedResponseData<Pair<Long, List<String>>> {
        val users = if (keyword.isBlank()) {
            userRepository.findAll(Pageable.ofSize(pageSize).withPage(page - 1))
        } else {
            userRepository.findAllByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                keyword,
                keyword,
                Pageable.ofSize(pageSize).withPage(page - 1)
            )
        }
        val userRoleMap = userRoleRelationRepository
            .findAllByUserIdIn(users.content.map { it.id })
            .groupBy { it.userId }
            .mapValues { it.value.map { relation -> relation.roleId.toString() } }

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

    @Transactional
    override fun bindRole(userId: Long, roleType: UserRoleType) {
        val roleEntity = userRoleService.getRoleEntityByType(roleType)

        val isAlreadyBound = getUserRolesByUserId(userId)
            .any { it == roleEntity.id.toString() }

        if (!isAlreadyBound) {
            userRoleRelationRepository.save(
                UserRoleRelationEntity(
                    id = snowIdGenerator.nextId(),
                    userId = userId,
                    roleId = roleEntity.id
                )
            )
        }
    }

    @Transactional
    override fun unbindRole(userId: Long, roleType: UserRoleType) {
        val roleEntity = userRoleService.getRoleEntityByType(roleType)

        this.userRoleRelationRepository.deleteByUserIdAndRoleId(userId, roleEntity.id)
    }
}
