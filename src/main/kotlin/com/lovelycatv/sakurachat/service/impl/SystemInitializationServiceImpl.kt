/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.service.impl

import com.lovelycatv.sakurachat.constants.SystemSettings
import com.lovelycatv.sakurachat.controller.manager.dto.ManagerCreateUserDTO
import com.lovelycatv.sakurachat.entity.UserEntity
import com.lovelycatv.sakurachat.exception.BusinessException
import com.lovelycatv.sakurachat.repository.UserRepository
import com.lovelycatv.sakurachat.service.SystemInitializationService
import com.lovelycatv.sakurachat.service.SystemSettingsService
import com.lovelycatv.sakurachat.service.UserRoleRelationService
import com.lovelycatv.sakurachat.service.UserRoleService
import com.lovelycatv.sakurachat.service.user.UserService
import com.lovelycatv.sakurachat.types.UserRoleType
import com.lovelycatv.vertex.log.logger
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SystemInitializationServiceImpl(
    private val systemSettingsService: SystemSettingsService,
    private val userService: UserService,
    private val userRepository: UserRepository,
    private val userRoleRelationService: UserRoleRelationService,
    private val userRoleService: UserRoleService
) : SystemInitializationService {
    private val logger = logger()

    override fun isSystemInitialized(): Boolean {
        return systemSettingsService.getSettings(SystemSettings.System.SYSTEM_INITIALIZED) { "false" }!!.toBoolean()
    }

    override fun isRootUserCreated(): Boolean {
        val rootRoleId = userRoleService.getRoleEntityByType(UserRoleType.ROOT).id
        return userRepository.findAll().any { user ->
            userRoleRelationService.getUserRolesByUserId(user.id).contains(rootRoleId.toString())
        }
    }

    @Transactional
    override fun createRootUser(managerCreateUserDTO: ManagerCreateUserDTO) {
        if (this.isRootUserCreated()) {
            throw BusinessException("root user already exists")
        }

        userService.createUser(managerCreateUserDTO, UserRoleType.ROOT)
        logger.info("Root user created successfully: ${managerCreateUserDTO.username}")
    }

    @Transactional
    override fun completeInitialization() {
        systemSettingsService.setSettings(SystemSettings.System.SYSTEM_INITIALIZED, "true")
        logger.info("System initialization completed successfully")
    }
}
