/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.service

import com.lovelycatv.sakurachat.controller.manager.dto.UpdateUserRoleRelationDTO
import com.lovelycatv.sakurachat.entity.UserRoleRelationEntity
import com.lovelycatv.sakurachat.repository.UserRoleRelationRepository
import com.lovelycatv.sakurachat.request.PaginatedResponseData
import com.lovelycatv.sakurachat.types.UserRoleType
import org.springframework.transaction.annotation.Transactional

interface UserRoleRelationService : BaseService<UserRoleRelationRepository, UserRoleRelationEntity, Long> {
    @Transactional
    fun updateUserRoleRelations(updateUserRoleRelationDTO: UpdateUserRoleRelationDTO)

    fun getUserRolesByUserId(userId: Long): List<String>

    fun search(keyword: String, page: Int, pageSize: Int): PaginatedResponseData<Pair<Long, List<String>>>

    @Transactional
    fun bindRole(userId: Long, roleType: UserRoleType)

    @Transactional
    fun unbindRole(userId: Long, roleType: UserRoleType)
}
