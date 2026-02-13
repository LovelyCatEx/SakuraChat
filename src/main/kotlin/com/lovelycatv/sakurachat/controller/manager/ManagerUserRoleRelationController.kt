/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.controller.manager

import com.lovelycatv.sakurachat.controller.manager.dto.ManagerUpdateUserRoleRelationDTO
import com.lovelycatv.sakurachat.request.ApiResponse
import com.lovelycatv.sakurachat.request.PageQuery
import com.lovelycatv.sakurachat.service.UserRoleRelationService
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/manager/user-role-relation")
@Validated
class ManagerUserRoleRelationController(
    private val userRoleRelationService: UserRoleRelationService
) {
    @GetMapping("/list")
    suspend fun listUserRoleRelations(@ModelAttribute pageQuery: PageQuery): ApiResponse<*> {
        return ApiResponse.success(
            userRoleRelationService.search("", pageQuery.page, pageQuery.pageSize)
        )
    }

    @GetMapping("/search")
    suspend fun searchUserRoleRelations(
        @RequestParam("keyword") keyword: String,
        @ModelAttribute pageQuery: PageQuery
    ): ApiResponse<*> {
        return ApiResponse.success(
            userRoleRelationService.search(keyword, pageQuery.page, pageQuery.pageSize)
        )
    }

    @GetMapping("/get-user-roles")
    suspend fun getUserRoles(@RequestParam("userId") userId: Long): ApiResponse<*> {
        return ApiResponse.success(
            userRoleRelationService.getUserRolesByUserId(userId)
        )
    }

    @PostMapping("/update")
    fun updateUserRoleRelations(@RequestBody managerUpdateUserRoleRelationDTO: ManagerUpdateUserRoleRelationDTO): ApiResponse<*> {
        userRoleRelationService.updateUserRoleRelations(managerUpdateUserRoleRelationDTO)
        return ApiResponse.success(null)
    }
}
