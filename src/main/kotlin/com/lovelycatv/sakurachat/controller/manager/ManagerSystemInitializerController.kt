/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.controller.manager

import com.lovelycatv.sakurachat.annotations.Unauthorized
import com.lovelycatv.sakurachat.controller.manager.dto.ManagerCreateUserDTO
import com.lovelycatv.sakurachat.request.ApiResponse
import com.lovelycatv.sakurachat.service.SystemInitializationService
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/manager/initializer")
@Validated
class ManagerSystemInitializerController(
    private val systemInitializationService: SystemInitializationService
) {
    @Unauthorized
    @GetMapping("/status")
    suspend fun getInitializationStatus(): ApiResponse<*> {
        return ApiResponse.success(systemInitializationService.isSystemInitialized())
    }

    @Unauthorized
    @GetMapping("/root-user-status")
    suspend fun getRootUserStatus(): ApiResponse<*> {
        return ApiResponse.success(systemInitializationService.isRootUserCreated())
    }

    @Unauthorized
    @PostMapping("/create-root-user")
    suspend fun createRootUser(@ModelAttribute managerCreateUserDTO: ManagerCreateUserDTO): ApiResponse<*> {
        systemInitializationService.createRootUser(managerCreateUserDTO)
        return ApiResponse.success(null)
    }

    @Unauthorized
    @PostMapping("/complete-initialization")
    suspend fun completeInitialization(): ApiResponse<*> {
        systemInitializationService.completeInitialization()
        return ApiResponse.success(null)
    }
}
