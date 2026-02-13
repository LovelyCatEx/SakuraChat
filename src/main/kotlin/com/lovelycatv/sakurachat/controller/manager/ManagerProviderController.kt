/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.controller.manager

import com.lovelycatv.sakurachat.constants.SystemRolePermissions
import com.lovelycatv.sakurachat.controller.manager.dto.ManagerCreateProviderDTO
import com.lovelycatv.sakurachat.controller.manager.dto.ManagerUpdateProviderDTO
import com.lovelycatv.sakurachat.request.ApiResponse
import com.lovelycatv.sakurachat.request.PageQuery
import com.lovelycatv.sakurachat.service.ProviderService
import com.lovelycatv.sakurachat.utils.toPaginatedResponseData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/manager/provider")
@Validated
class ManagerProviderController(
    private val providerService: ProviderService
) {
    @PreAuthorize(SystemRolePermissions.PERMISSION_ROOT_ADMIN)
    @GetMapping("/list")
    suspend fun listProviders(@ModelAttribute pageQuery: PageQuery): ApiResponse<*> {
        return ApiResponse.success(
            providerService
                .listByPage(pageQuery.page, pageQuery.pageSize)
                .toPaginatedResponseData()
        )
    }

    @PreAuthorize(SystemRolePermissions.PERMISSION_ROOT_ADMIN)
    @GetMapping("/search")
    suspend fun searchProviders(
        @RequestParam("keyword") keyword: String,
        @ModelAttribute pageQuery: PageQuery
    ): ApiResponse<*> {
        return ApiResponse.success(
            providerService.search(keyword, pageQuery.page, pageQuery.pageSize)
        )
    }

    @PreAuthorize(SystemRolePermissions.PERMISSION_ROOT_ADMIN)
    @GetMapping("/getById")
    suspend fun getProviderById(@RequestParam("id") id: Long): ApiResponse<*> {
        return ApiResponse.success(
            providerService.getByIdOrThrow(id)
        )
    }

    @PreAuthorize(SystemRolePermissions.PERMISSION_ROOT_ADMIN)
    @PostMapping("/create")
    suspend fun createProvider(@ModelAttribute managerCreateProviderDTO: ManagerCreateProviderDTO): ApiResponse<*> {
        providerService.createProvider(managerCreateProviderDTO)
        return ApiResponse.success(null)
    }

    @PreAuthorize(SystemRolePermissions.PERMISSION_ROOT_ADMIN)
    @PostMapping("/update")
    suspend fun updateProvider(@ModelAttribute managerUpdateProviderDTO: ManagerUpdateProviderDTO): ApiResponse<*> {
        return ApiResponse.success(
            providerService.updateProvider(managerUpdateProviderDTO)
        )
    }

    @PreAuthorize(SystemRolePermissions.PERMISSION_ROOT_ADMIN)
    @PostMapping("/delete")
    suspend fun deleteProvider(@RequestParam("id") id: Long): ApiResponse<*> {
        return ApiResponse.success(
            withContext(Dispatchers.IO) {
                providerService.getRepository().deleteById(id)
            }
        )
    }
}