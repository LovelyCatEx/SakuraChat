/*
 * Copyright 2025 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */
package com.lovelycatv.sakurachat.service

import com.lovelycatv.sakurachat.controller.manager.dto.ManagerCreateUserDTO
import com.lovelycatv.sakurachat.controller.manager.dto.UpdateUserDTO
import com.lovelycatv.sakurachat.entity.UserEntity
import com.lovelycatv.sakurachat.repository.UserRepository
import com.lovelycatv.sakurachat.request.PaginatedResponseData
import com.lovelycatv.sakurachat.types.ThirdPartyPlatform
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

interface UserService : UserDetailsService, BaseService<UserRepository, UserEntity, Long> {
    suspend fun register(username: String, password: String, email: String, emailCode: String)

    suspend fun getUserByThirdPartyAccount(
        platform: ThirdPartyPlatform,
        accountId: String
    ): UserEntity?

    suspend fun getUserProfileById(userId: Long): UserEntity

    suspend fun updateUser(updateUserDTO: UpdateUserDTO)

    suspend fun createUser(managerCreateUserDTO: ManagerCreateUserDTO)

    suspend fun search(keyword: String, page: Int, pageSize: Int): PaginatedResponseData<UserEntity>
}