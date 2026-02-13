/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.controller.manager

import com.lovelycatv.sakurachat.constants.SystemRolePermissions
import com.lovelycatv.sakurachat.controller.manager.dto.ManagerCreateUserDTO
import com.lovelycatv.sakurachat.controller.manager.dto.ManagerUpdateUserDTO
import com.lovelycatv.sakurachat.request.ApiResponse
import com.lovelycatv.sakurachat.request.PageQuery
import com.lovelycatv.sakurachat.service.user.UserService
import com.lovelycatv.sakurachat.utils.toPaginatedResponseData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/manager/user")
@Validated
class ManagerUserController(
    private val userService: UserService
) {
    @PreAuthorize(SystemRolePermissions.PERMISSION_ROOT_ADMIN)
    @GetMapping("/list")
    suspend fun listUsers(@ModelAttribute pageQuery: PageQuery): ApiResponse<*> {
        return ApiResponse.success(
            userService
                .listByPage(pageQuery.page, pageQuery.pageSize)
                .toPaginatedResponseData()
        )
    }

    @PreAuthorize(SystemRolePermissions.PERMISSION_ROOT_ADMIN)
    @GetMapping("/search")
    suspend fun searchUsers(
        @RequestParam("keyword") keyword: String,
        @ModelAttribute pageQuery: PageQuery
    ): ApiResponse<*> {
        return ApiResponse.success(
            userService.search(keyword, pageQuery.page, pageQuery.pageSize)
        )
    }

    @PreAuthorize(SystemRolePermissions.PERMISSION_ROOT_ADMIN)
    @GetMapping("/getById")
    suspend fun getUserById(@RequestParam("id") id: Long): ApiResponse<*> {
        return ApiResponse.success(
            userService.getRepository().findById(id).orElse(null)
        )
    }

    @PreAuthorize(SystemRolePermissions.PERMISSION_ROOT_ONLY)
    @PostMapping("/create")
    suspend fun createUser(@ModelAttribute managerCreateUserDTO: ManagerCreateUserDTO): ApiResponse<*> {
        userService.createUser(managerCreateUserDTO)
        return ApiResponse.success(null)
    }

    @PreAuthorize(SystemRolePermissions.PERMISSION_ROOT_ONLY)
    @PostMapping("/update")
    suspend fun updateUser(@ModelAttribute managerUpdateUserDTO: ManagerUpdateUserDTO): ApiResponse<*> {
        userService.updateUser(managerUpdateUserDTO)
        return ApiResponse.success(null)
    }

    @PreAuthorize(SystemRolePermissions.PERMISSION_ROOT_ONLY)
    @PostMapping("/delete")
    suspend fun deleteUser(@RequestParam("id") id: Long): ApiResponse<*> {
        return ApiResponse.success(
            withContext(Dispatchers.IO) {
                userService.getRepository().deleteById(id)
            }
        )
    }
}
