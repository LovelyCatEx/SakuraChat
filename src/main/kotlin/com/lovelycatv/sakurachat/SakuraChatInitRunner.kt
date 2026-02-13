/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat

import com.lovelycatv.sakurachat.entity.UserRoleEntity
import com.lovelycatv.sakurachat.exception.BusinessException
import com.lovelycatv.sakurachat.service.UserRoleService
import com.lovelycatv.sakurachat.types.UserRoleType
import com.lovelycatv.sakurachat.utils.SnowIdGenerator
import com.lovelycatv.sakurachat.utils.toJSONString
import com.lovelycatv.vertex.log.logger
import kotlinx.coroutines.runBlocking
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class SakuraChatInitRunner(private val userRoleService: UserRoleService, private val snowIdGenerator: SnowIdGenerator) : CommandLineRunner {
    private val logger = logger()

    override fun run(vararg args: String?) {
        logger.info("===================================================")
        logger.info("      Starting SakuraChat Initialization...        ")
        logger.info("===================================================")
        runBlocking {
            initDatabase()
        }
        logger.info("================================================")
        logger.info("       SakuraChat Initialization Finished       ")
        logger.info("================================================")
    }

    private fun initDatabase() {
        // Init UserRoles
        logger.info("+ Checking user_roles table...")
        UserRoleType.entries.forEach {
            try {
                val roleEntity = userRoleService.getRoleEntityByType(it)
                logger.info("  √ UserRole ${it.name}#${it.roleName} is present, data: ${roleEntity.toJSONString()}")
            } catch (e: BusinessException) {
                logger.warn("  × User role ${it.name}#${it.roleName} not found in database, inserting...", e)
                userRoleService.getRepository().save(
                    UserRoleEntity(
                        id = snowIdGenerator.nextId(),
                        name = it.roleName,
                        description = it.roleName
                    )
                )
            }
        }
    }
}