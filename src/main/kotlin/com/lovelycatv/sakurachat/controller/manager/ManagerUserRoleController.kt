/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.controller.manager

import com.lovelycatv.sakurachat.controller.manager.dto.ManagerCreateUserRoleDTO
import com.lovelycatv.sakurachat.controller.manager.dto.UpdateUserRoleDTO
import com.lovelycatv.sakurachat.request.ApiResponse
import com.lovelycatv.sakurachat.request.PageQuery
import com.lovelycatv.sakurachat.service.UserRoleService
import com.lovelycatv.sakurachat.utils.toPaginatedResponseData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.data.domain.Pageable
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/manager/user-role")
@Validated
class ManagerUserRoleController(
    private val userRoleService: UserRoleService
) {
    @GetMapping("/list")
    suspend fun listUserRoles(@ModelAttribute pageQuery: PageQuery): ApiResponse<*> {
        return ApiResponse.success(
            userRoleService.getRepository().findAll(
                Pageable
                    .ofSize(pageQuery.pageSize)
                    .withPage(pageQuery.page - 1)
            ).toPaginatedResponseData()
        )
    }

    @PostMapping("/create")
    suspend fun createUserRole(@ModelAttribute managerCreateUserRoleDTO: ManagerCreateUserRoleDTO): ApiResponse<*> {
        userRoleService.createUserRole(managerCreateUserRoleDTO)
        return ApiResponse.success(null)
    }

    @PostMapping("/update")
    suspend fun updateUserRole(@ModelAttribute updateUserRoleDTO: UpdateUserRoleDTO): ApiResponse<*> {
        userRoleService.updateUserRole(updateUserRoleDTO)
        return ApiResponse.success(null)
    }

    @PostMapping("/delete")
    suspend fun deleteUserRole(@RequestParam("id") id: Long): ApiResponse<*> {
        return ApiResponse.success(
            withContext(Dispatchers.IO) {
                userRoleService.getRepository().deleteById(id)
            }
        )
    }
}
