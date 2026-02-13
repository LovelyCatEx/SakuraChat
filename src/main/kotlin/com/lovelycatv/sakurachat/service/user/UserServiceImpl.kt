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
import com.lovelycatv.sakurachat.exception.BusinessException
import com.lovelycatv.sakurachat.repository.UserRepository
import com.lovelycatv.sakurachat.repository.UserThirdPartyAccountRelationRepository
import com.lovelycatv.sakurachat.request.PaginatedResponseData
import com.lovelycatv.sakurachat.service.SystemSettingsService
import com.lovelycatv.sakurachat.service.ThirdPartyAccountService
import com.lovelycatv.sakurachat.service.UserPointsService
import com.lovelycatv.sakurachat.service.UserRoleRelationService
import com.lovelycatv.sakurachat.service.UserRoleService
import com.lovelycatv.sakurachat.service.mail.MailService
import com.lovelycatv.sakurachat.service.request.UserPointsConsumeRequest
import com.lovelycatv.sakurachat.types.PointsChangesReason
import com.lovelycatv.sakurachat.types.ThirdPartyPlatform
import com.lovelycatv.sakurachat.types.UserRoleType
import com.lovelycatv.sakurachat.utils.toPaginatedResponseData
import com.lovelycatv.vertex.log.logger
import okio.withLock
import org.springframework.data.domain.Pageable
import org.springframework.security.core.authority.SimpleGrantedAuthority
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
    private val systemSettingsService: SystemSettingsService,
    private val userRoleRelationService: UserRoleRelationService,
    private val userRoleService: UserRoleService
) : UserService {
    private val logger = logger()

    companion object {
        val registrationEmailCodeMap = mutableMapOf<String, Pair<Long, String>>()
        val passwordResetEmailCodeMap = mutableMapOf<String, Pair<Long, String>>()
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
                afterNewUserSaved(it, UserRoleType.USER)
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

    override fun sendPasswordResetEmail(email: String): String {
        if (userRepository.findByEmail(email) == null) {
            throw BusinessException("邮箱 $email 未注册")
        }

        if (passwordResetEmailCodeMap.containsKey(email)) {
            if (System.currentTimeMillis() - passwordResetEmailCodeMap[email]!!.first <= 60 * 1000L) {
                throw BusinessException("request email code frequently, please try again later")
            }
        }

        val code = (100000..999999).random().toString()

        mailService.sendResetPasswordEmail(email, code)

        return code.also {
            passwordResetEmailCodeMap[email] = System.currentTimeMillis() to code
        }
    }

    override fun resetPassword(email: String, emailCode: String, newPassword: String) {
        val codePair = passwordResetEmailCodeMap[email]
            ?: throw BusinessException("验证码不存在或已过期")

        if (System.currentTimeMillis() - codePair.first > 5 * 60 * 1000L) {
            passwordResetEmailCodeMap.remove(email)
            throw BusinessException("验证码已过期")
        }

        if (codePair.second != emailCode) {
            throw BusinessException("验证码错误")
        }

        val user = userRepository.findByEmail(email)
            ?: throw BusinessException("用户不存在")

        userIdMutex.withLock {
            userRepository.save(
                user.apply {
                    this.updatePassword(passwordEncoder.encode(newPassword))
                }
            )
        }

        passwordResetEmailCodeMap.remove(email)

        logger.info("Password reset successfully for user: ${user.username}")
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
    override fun updateUser(managerUpdateUserDTO: ManagerUpdateUserDTO) {
        val existing = this.getByIdOrThrow(managerUpdateUserDTO.id)

        if (managerUpdateUserDTO.nickname != null) {
            existing.nickname = managerUpdateUserDTO.nickname
        }

        if (managerUpdateUserDTO.email != null) {
            existing.email = managerUpdateUserDTO.email
        }

        if (managerUpdateUserDTO.points != null) {
            val delta = managerUpdateUserDTO.points - existing.points
            if (delta != 0L) {
                this.userPointsService.consumePoints(
                    userId = managerUpdateUserDTO.id,
                    request = UserPointsConsumeRequest(
                        reason = PointsChangesReason.ADMIN,
                        consumedPoints = - delta,
                        afterBalance = managerUpdateUserDTO.points
                    )
                )

                existing.points = managerUpdateUserDTO.points
            }
        }


        getRepository().save(existing)
    }

    override fun createUser(managerCreateUserDTO: ManagerCreateUserDTO, userRole: UserRoleType) {
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
                afterNewUserSaved(it, userRole)
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

        user.setInnerAuthorities(
            getUserRoles(user.id).map {
                SimpleGrantedAuthority(it.roleName)
            }
        )

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

    override fun getUserRoles(userId: Long): List<UserRoleType> {
        val roleEntityIds = userRoleRelationService.getUserRolesByUserId(userId)
        if (roleEntityIds.isEmpty()) {
            return emptyList()
        }

        return userRoleService
            .getByIds(roleEntityIds.map { it.toLong() })
            .map {
                UserRoleType.getByRoleName(it.name) ?: throw BusinessException("Role $it not found")
            }
    }

    // Please preserve the inline modifier to make sure the @Transactional works
    private inline fun afterNewUserSaved(user: UserEntity, defaultUserRole: UserRoleType) {
        val initialPoints = systemSettingsService.getAllSettingsLazy().userRegistration.initialPoints
        this.userPointsService.consumePoints(
            userId = user.id,
            request = UserPointsConsumeRequest(
                reason = PointsChangesReason.REGISTER,
                consumedPoints = - abs(initialPoints),
                afterBalance = initialPoints
            )
        )

        this.userRoleRelationService.bindRole(user.id, defaultUserRole)
    }

    private fun findNextUserId(): Long {
        return (getRepository().findMaxId() ?: 0) + 1
    }
}