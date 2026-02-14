/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.controller.manager

import com.lovelycatv.sakurachat.constants.SystemRolePermissions
import com.lovelycatv.sakurachat.controller.manager.dto.ManagerCreatePointsCdKeyDTO
import com.lovelycatv.sakurachat.controller.manager.dto.ManagerUpdatePointsCdKeyDTO
import com.lovelycatv.sakurachat.request.ApiResponse
import com.lovelycatv.sakurachat.request.PageQuery
import com.lovelycatv.sakurachat.service.PointsCdKeyService
import com.lovelycatv.sakurachat.types.UserAuthentication
import com.lovelycatv.sakurachat.utils.toPaginatedResponseData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/manager/points-cdkey")
@Validated
class ManagerPointsCdKeyController(
    private val pointsCdKeyService: PointsCdKeyService
) {
    @PreAuthorize(SystemRolePermissions.PERMISSION_ROOT_ADMIN)
    @GetMapping("/list")
    suspend fun listPointsCdKeys(@ModelAttribute pageQuery: PageQuery): ApiResponse<*> {
        return ApiResponse.success(
            pointsCdKeyService
                .listByPage(pageQuery.page, pageQuery.pageSize)
                .toPaginatedResponseData()
        )
    }

    @PreAuthorize(SystemRolePermissions.PERMISSION_ROOT_ADMIN)
    @GetMapping("/search")
    suspend fun searchPointsCdKeys(
        @RequestParam("keyword") keyword: String,
        @ModelAttribute pageQuery: PageQuery
    ): ApiResponse<*> {
        return ApiResponse.success(
            pointsCdKeyService.search(keyword, pageQuery)
        )
    }

    @PreAuthorize(SystemRolePermissions.PERMISSION_ROOT_ADMIN)
    @GetMapping("/getById")
    suspend fun getPointsCdKeyById(@RequestParam("id") id: Long): ApiResponse<*> {
        return ApiResponse.success(
            pointsCdKeyService.getByIdOrThrow(id)
        )
    }

    @PreAuthorize(SystemRolePermissions.PERMISSION_ROOT_ADMIN)
    @PostMapping("/create")
    suspend fun createPointsCdKey(
        userAuthentication: UserAuthentication,
        @ModelAttribute managerCreatePointsCdKeyDTO: ManagerCreatePointsCdKeyDTO
    ): ApiResponse<*> {
        val cdKeyEntity = pointsCdKeyService.createCdKey(
            cdKey = managerCreatePointsCdKeyDTO.cdKey,
            points = managerCreatePointsCdKeyDTO.points,
            generatedBy = userAuthentication.userId
        )
        return ApiResponse.success(cdKeyEntity)
    }

    @PreAuthorize(SystemRolePermissions.PERMISSION_ROOT_ADMIN)
    @PostMapping("/update")
    suspend fun updatePointsCdKey(@ModelAttribute managerUpdatePointsCdKeyDTO: ManagerUpdatePointsCdKeyDTO): ApiResponse<*> {
        val updatedEntity = pointsCdKeyService.updateCdKey(managerUpdatePointsCdKeyDTO)
        return ApiResponse.success(updatedEntity)
    }

    @PreAuthorize(SystemRolePermissions.PERMISSION_ROOT_ADMIN)
    @PostMapping("/delete")
    suspend fun deletePointsCdKey(@RequestParam("id") id: Long): ApiResponse<*> {
        withContext(Dispatchers.IO) {
            pointsCdKeyService.getRepository().deleteById(id)
        }
        return ApiResponse.success(null)
    }
}