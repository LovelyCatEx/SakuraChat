/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.service.user

import com.lovelycatv.sakurachat.controller.manager.dto.ManagerCreateUserDTO
import com.lovelycatv.sakurachat.controller.manager.dto.ManagerUpdateUserDTO
import com.lovelycatv.sakurachat.entity.UserEntity
import com.lovelycatv.sakurachat.repository.UserRepository
import com.lovelycatv.sakurachat.request.PaginatedResponseData
import com.lovelycatv.sakurachat.service.BaseService
import com.lovelycatv.sakurachat.types.ThirdPartyPlatform
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.transaction.annotation.Transactional

interface UserService : UserDetailsService, BaseService<UserRepository, UserEntity, Long> {
    @Transactional
    fun register(username: String, password: String, email: String, emailCode: String)

    /**
     * Send a registration email code to target email address
     *
     * @param email Target email address
     * @return email code has been sent
     */
    fun sendRegisterEmail(email: String): String

    fun getUserByThirdPartyAccount(
        platform: ThirdPartyPlatform,
        accountId: String
    ): UserEntity?

    fun getUserProfileById(userId: Long): UserEntity

    @Transactional

    fun updateUser(managerUpdateUserDTO: ManagerUpdateUserDTO)

    @Transactional
    fun createUser(managerCreateUserDTO: ManagerCreateUserDTO)

    fun search(keyword: String, page: Int, pageSize: Int): PaginatedResponseData<UserEntity>
}