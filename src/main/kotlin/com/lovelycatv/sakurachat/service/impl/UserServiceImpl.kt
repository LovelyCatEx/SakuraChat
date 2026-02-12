/*
 * Copyright 2025 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */
package com.lovelycatv.sakurachat.service.impl

import com.lovelycatv.sakurachat.controller.manager.dto.ManagerCreateUserDTO
import com.lovelycatv.sakurachat.controller.manager.dto.UpdateUserDTO
import com.lovelycatv.sakurachat.entity.UserEntity
import com.lovelycatv.sakurachat.exception.BusinessException
import com.lovelycatv.sakurachat.repository.UserRepository
import com.lovelycatv.sakurachat.repository.UserThirdPartyAccountRelationRepository
import com.lovelycatv.sakurachat.request.PaginatedResponseData
import com.lovelycatv.sakurachat.service.ThirdPartyAccountService
import com.lovelycatv.sakurachat.service.UserService
import com.lovelycatv.sakurachat.types.ThirdPartyPlatform
import com.lovelycatv.sakurachat.utils.SnowIdGenerator
import com.lovelycatv.sakurachat.utils.toPaginatedResponseData
import com.lovelycatv.vertex.log.logger
import jakarta.annotation.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.data.domain.Pageable
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import kotlin.jvm.optionals.getOrNull

@Service
class UserServiceImpl : UserService {
    @Resource
    private lateinit var userRepository: UserRepository
    @Resource
    private lateinit var passwordEncoder: PasswordEncoder
    @Resource
    private lateinit var snowIdGenerator: SnowIdGenerator
    @Resource
    private lateinit var userThirdPartyAccountRelationRepository: UserThirdPartyAccountRelationRepository
    @Resource
    private lateinit var thirdPartyAccountService: ThirdPartyAccountService

    private val logger = logger()

    override suspend fun register(username: String, password: String, email: String, emailCode: String) {
        val userByName = withContext(Dispatchers.IO) {
            userRepository.findByUsername(username)
        }
        if (userByName != null) {
            throw BusinessException("user $username already registered")
        }

        val userByEmail = withContext(Dispatchers.IO) {
            userRepository.findByEmail(email)
        }
        if (userByEmail != null) {
            throw BusinessException("user $email already registered")
        }

        withContext(Dispatchers.IO) {
            userRepository.save(
                UserEntity(
                    snowIdGenerator.nextId(),
                    username,
                    passwordEncoder.encode(password),
                    username,
                    email
                )
            )
        }
    }

    override suspend fun getUserByThirdPartyAccount(
        platform: ThirdPartyPlatform,
        accountId: String
    ): UserEntity? {
        val thirdPartyAccount = this.thirdPartyAccountService
            .getAccountByPlatformAndAccountId(platform, accountId)
            ?: return null

        val userId = withContext(Dispatchers.IO) {
            userThirdPartyAccountRelationRepository
                .findByThirdPartyAccountId(thirdPartyAccount.id)
                .also { relations ->
                    if (relations.size > 1) {
                        logger.warn("Third party account $accountId of platform ${platform.name} has bind more than 1 user, " +
                                "userIds: ${relations.joinToString { it.primaryKey.userId.toString() }}"
                        )
                    }
                }
                .firstOrNull()
                ?.primaryKey?.userId
        } ?: return null

        return withContext(Dispatchers.IO) {
            userRepository.findById(userId)
        }.orElse(null)
    }

    override suspend fun getUserProfileById(userId: Long): UserEntity {
        return withContext(Dispatchers.IO) {
            getRepository().findById(userId)
        }.getOrNull() ?: throw BusinessException("User $userId not found")
    }

    override suspend fun updateUser(updateUserDTO: UpdateUserDTO) {
        val existing = this.getByIdOrThrow(updateUserDTO.id)

        if (updateUserDTO.nickname != null) {
            existing.nickname = updateUserDTO.nickname
        }

        if (updateUserDTO.email != null) {
            existing.email = updateUserDTO.email
        }

        if (updateUserDTO.points != null) {
            existing.points = updateUserDTO.points
        }

        withContext(Dispatchers.IO) {
            getRepository().save(existing)
        }
    }

    override suspend fun createUser(managerCreateUserDTO: ManagerCreateUserDTO) {
        val userByName = withContext(Dispatchers.IO) {
            userRepository.findByUsername(managerCreateUserDTO.username)
        }

        if (userByName != null) {
            throw BusinessException("user ${managerCreateUserDTO.username} already registered")
        }

        val userByEmail = withContext(Dispatchers.IO) {
            userRepository.findByEmail(managerCreateUserDTO.email)
        }

        if (userByEmail != null) {
            throw BusinessException("user ${managerCreateUserDTO.email} already registered")
        }

        withContext(Dispatchers.IO) {
            getRepository().save(
                UserEntity(
                    id = snowIdGenerator.nextId(),
                    username = managerCreateUserDTO.username,
                    password = passwordEncoder.encode(managerCreateUserDTO.password),
                    nickname = managerCreateUserDTO.nickname,
                    email = managerCreateUserDTO.email,
                    points = managerCreateUserDTO.points
                )
            )
        }
    }

    override fun loadUserByUsername(username: String?): UserDetails {
        if (username == null) {
            throw BusinessException("Username could not be null")
        }

        val user = userRepository.findByUsername(username)
            ?: userRepository.findByEmail(username)
            ?: throw BusinessException("User $username not found")

        return user
    }

    override fun getRepository(): UserRepository {
        return this.userRepository
    }

    override suspend fun search(keyword: String, page: Int, pageSize: Int): PaginatedResponseData<UserEntity> {
        if (keyword.isBlank()) {
            return this.listByPage(page, pageSize).toPaginatedResponseData()
        }

        return withContext(Dispatchers.IO) {
            getRepository().findAllByUsernameContainingIgnoreCaseOrNicknameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                keyword,
                keyword,
                keyword,
                Pageable.ofSize(pageSize).withPage(page - 1)
            )
        }.toPaginatedResponseData()
    }
}