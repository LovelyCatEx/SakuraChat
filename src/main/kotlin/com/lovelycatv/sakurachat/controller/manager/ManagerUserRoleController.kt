/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.controller.manager

import com.lovelycatv.sakurachat.constants.SystemRolePermissions
import com.lovelycatv.sakurachat.controller.manager.dto.ManagerCreateUserRoleDTO
import com.lovelycatv.sakurachat.controller.manager.dto.ManagerUpdateUserRoleDTO
import com.lovelycatv.sakurachat.request.ApiResponse
import com.lovelycatv.sakurachat.request.PageQuery
import com.lovelycatv.sakurachat.service.UserRoleService
import com.lovelycatv.sakurachat.utils.toPaginatedResponseData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/manager/user-role")
@Validated
class ManagerUserRoleController(
    private val userRoleService: UserRoleService
) {
    @PreAuthorize(SystemRolePermissions.PERMISSION_ROOT_ADMIN)
    @GetMapping("/list")
    suspend fun listUserRoles(@ModelAttribute pageQuery: PageQuery): ApiResponse<*> {
        return ApiResponse.success(
            userRoleService
                .listByPage(pageQuery.page, pageQuery.pageSize)
                .toPaginatedResponseData()
        )
    }

    @PreAuthorize(SystemRolePermissions.PERMISSION_ROOT_ADMIN)
    @GetMapping("/search")
    suspend fun searchUserRoles(
        @RequestParam("keyword") keyword: String,
        @ModelAttribute pageQuery: PageQuery
    ): ApiResponse<*> {
        return ApiResponse.success(
            userRoleService.search(keyword, pageQuery.page, pageQuery.pageSize)
        )
    }

    @PreAuthorize(SystemRolePermissions.PERMISSION_ROOT_ONLY)
    @PostMapping("/create")
    suspend fun createUserRole(@ModelAttribute managerCreateUserRoleDTO: ManagerCreateUserRoleDTO): ApiResponse<*> {
        userRoleService.createUserRole(managerCreateUserRoleDTO)
        return ApiResponse.success(null)
    }

    @PreAuthorize(SystemRolePermissions.PERMISSION_ROOT_ONLY)
    @PostMapping("/update")
    suspend fun updateUserRole(@ModelAttribute managerUpdateUserRoleDTO: ManagerUpdateUserRoleDTO): ApiResponse<*> {
        userRoleService.updateUserRole(managerUpdateUserRoleDTO)
        return ApiResponse.success(null)
    }

    @PreAuthorize(SystemRolePermissions.PERMISSION_ROOT_ONLY)
    @PostMapping("/delete")
    suspend fun deleteUserRole(@RequestParam("id") id: Long): ApiResponse<*> {
        return ApiResponse.success(
            withContext(Dispatchers.IO) {
                userRoleService.getRepository().deleteById(id)
            }
        )
    }
}
