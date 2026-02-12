/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.service.user

import com.lovelycatv.sakurachat.controller.manager.dto.ManagerCreateUserDTO
import com.lovelycatv.sakurachat.controller.manager.dto.UpdateUserDTO
import com.lovelycatv.sakurachat.entity.UserEntity
import com.lovelycatv.sakurachat.exception.BusinessException
import com.lovelycatv.sakurachat.repository.UserRepository
import com.lovelycatv.sakurachat.repository.UserThirdPartyAccountRelationRepository
import com.lovelycatv.sakurachat.request.PaginatedResponseData
import com.lovelycatv.sakurachat.service.SystemSettingsService
import com.lovelycatv.sakurachat.service.ThirdPartyAccountService
import com.lovelycatv.sakurachat.service.UserPointsService
import com.lovelycatv.sakurachat.service.mail.MailService
import com.lovelycatv.sakurachat.service.request.UserPointsConsumeRequest
import com.lovelycatv.sakurachat.types.PointsChangesReason
import com.lovelycatv.sakurachat.types.ThirdPartyPlatform
import com.lovelycatv.sakurachat.utils.toPaginatedResponseData
import com.lovelycatv.vertex.log.logger
import okio.withLock
import org.springframework.data.domain.Pageable
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.locks.ReentrantLock
import kotlin.jvm.optionals.getOrNull
import kotlin.math.abs

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val userThirdPartyAccountRelationRepository: UserThirdPartyAccountRelationRepository,
    private val thirdPartyAccountService: ThirdPartyAccountService,
    private val mailService: MailService,
    private val userPointsService: UserPointsService,
    private val systemSettingsService: SystemSettingsService
) : UserService {
    private val logger = logger()

    companion object {
        val registrationEmailCodeMap = mutableMapOf<String, Pair<Long, String>>()
    }

    /**
     * When a method operating user table by INSERT / UPDATE / DELETE,
     * corresponding method should be locked by userIdMutex.
     */
    private val userIdMutex = ReentrantLock()

    @Transactional
    override fun register(username: String, password: String, email: String, emailCode: String) {
        val userByName = userRepository.findByUsername(username)

        if (userByName != null) {
            throw BusinessException("user $username already registered")
        }

        val userByEmail = userRepository.findByEmail(email)

        if (userByEmail != null) {
            throw BusinessException("user $email already registered")
        }

        val correctEmailCode = registrationEmailCodeMap[email]?.let {
            // send time
            if (System.currentTimeMillis() - it.first > 10 * 60 * 1000L) {
                throw BusinessException("email code is expired")
            }

            // email code
            it.second
        } ?: throw BusinessException("email code is invalid")

        if (emailCode != correctEmailCode) {
            throw BusinessException("your email code is invalid")
        }

        registrationEmailCodeMap.remove(email)

        this.userIdMutex.withLock {
            userRepository.save(
                UserEntity(
                    findNextUserId(),
                    username,
                    passwordEncoder.encode(password),
                    username,
                    email
                )
            ).also {
                this.userPointsService.consumePoints(
                    userId = it.id,
                    request = UserPointsConsumeRequest(
                        reason = PointsChangesReason.REGISTER,
                        consumedPoints = -abs(systemSettingsService.getAllSettingsLazy().userRegistration.initialPoints)
                    )
                )
            }
        }
    }

    override fun sendRegisterEmail(email: String): String {
        if (registrationEmailCodeMap.containsKey(email)) {
            if (System.currentTimeMillis() - registrationEmailCodeMap[email]!!.first <= 60 * 1000L) {
                throw BusinessException("request email code frequently, please try again later")
            }
        }

        val code = (100000..999999).random().toString()

        mailService.sendRegisterEmail(email, code)

        return code.also {
            registrationEmailCodeMap[email] = System.currentTimeMillis() to code
        }
    }

    override fun getUserByThirdPartyAccount(
        platform: ThirdPartyPlatform,
        accountId: String
    ): UserEntity? {
        val thirdPartyAccount = this.thirdPartyAccountService
            .getAccountByPlatformAndAccountId(platform, accountId)
            ?: return null

        val userId = (userThirdPartyAccountRelationRepository
            .findByThirdPartyAccountId(thirdPartyAccount.id)
            .also { relations ->
                if (relations.size > 1) {
                    logger.warn(
                        "Third party account $accountId of platform ${platform.name} has bind more than 1 user, " +
                                "userIds: ${relations.joinToString { it.primaryKey.userId.toString() }}"
                    )
                }
            }
            .firstOrNull()
            ?.primaryKey?.userId) ?: return null

        return userRepository.findById(userId).orElse(null)
    }

    override fun getUserProfileById(userId: Long): UserEntity {
        return getRepository().findById(userId).getOrNull()
            ?: throw BusinessException("User $userId not found")
    }

    @Transactional
    override fun updateUser(updateUserDTO: UpdateUserDTO) {
        val existing = this.getByIdOrThrow(updateUserDTO.id)

        if (updateUserDTO.nickname != null) {
            existing.nickname = updateUserDTO.nickname
        }

        if (updateUserDTO.email != null) {
            existing.email = updateUserDTO.email
        }

        if (updateUserDTO.points != null) {
            val delta = updateUserDTO.points - existing.points
            if (delta != 0L) {
                this.userPointsService.consumePoints(
                    userId = updateUserDTO.id,
                    request = UserPointsConsumeRequest(
                        reason = PointsChangesReason.ADMIN,
                        consumedPoints = - delta
                    )
                )

                existing.points = updateUserDTO.points
            }
        }


        getRepository().save(existing)
    }

    override fun createUser(managerCreateUserDTO: ManagerCreateUserDTO) {
        val userByName = userRepository.findByUsername(managerCreateUserDTO.username)

        if (userByName != null) {
            throw BusinessException("user ${managerCreateUserDTO.username} already registered")
        }

        val userByEmail = userRepository.findByEmail(managerCreateUserDTO.email)

        if (userByEmail != null) {
            throw BusinessException("user ${managerCreateUserDTO.email} already registered")
        }

        this.userIdMutex.withLock {
            getRepository().save(
                UserEntity(
                    id = findNextUserId(),
                    username = managerCreateUserDTO.username,
                    password = passwordEncoder.encode(managerCreateUserDTO.password),
                    nickname = managerCreateUserDTO.nickname,
                    email = managerCreateUserDTO.email,
                    points = managerCreateUserDTO.points
                )
            ).also {
                this.userPointsService.consumePoints(
                    userId = it.id,
                    request = UserPointsConsumeRequest(
                        reason = PointsChangesReason.REGISTER,
                        consumedPoints = -abs(systemSettingsService.getAllSettingsLazy().userRegistration.initialPoints)
                    )
                )
            }
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

    override fun search(keyword: String, page: Int, pageSize: Int): PaginatedResponseData<UserEntity> {
        if (keyword.isBlank()) {
            return this.listByPage(page, pageSize).toPaginatedResponseData()
        }

        return getRepository().findAllByUsernameContainingIgnoreCaseOrNicknameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            keyword,
            keyword,
            keyword,
            Pageable.ofSize(pageSize).withPage(page - 1)
        ).toPaginatedResponseData()
    }

    private fun findNextUserId(): Long {
        return (getRepository().findMaxId() ?: 0) + 1
    }
}