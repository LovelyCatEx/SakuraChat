/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.controller.manager

import com.lovelycatv.sakurachat.controller.manager.dto.ManagerCreateProviderDTO
import com.lovelycatv.sakurachat.controller.manager.dto.UpdateProviderDTO
import com.lovelycatv.sakurachat.request.ApiResponse
import com.lovelycatv.sakurachat.request.PageQuery
import com.lovelycatv.sakurachat.service.ProviderService
import com.lovelycatv.sakurachat.utils.toPaginatedResponseData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.data.domain.Pageable
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/manager/provider")
@Validated
class ManagerProviderController(
    private val providerService: ProviderService
) {
    @GetMapping("/list")
    suspend fun listProviders(@ModelAttribute pageQuery: PageQuery): ApiResponse<*> {
        return ApiResponse.success(
            providerService.getRepository().findAll(
                Pageable
                    .ofSize(pageQuery.pageSize)
                    .withPage(pageQuery.page - 1)
            ).toPaginatedResponseData()
        )
    }

    @PostMapping("/create")
    suspend fun createProvider(@ModelAttribute managerCreateProviderDTO: ManagerCreateProviderDTO): ApiResponse<*> {
        providerService.createProvider(managerCreateProviderDTO)
        return ApiResponse.success(null)
    }

    @PostMapping("/update")
    suspend fun updateProvider(@ModelAttribute updateProviderDTO: UpdateProviderDTO): ApiResponse<*> {
        return ApiResponse.success(
            providerService.updateProvider(updateProviderDTO)
        )
    }

    @PostMapping("/delete")
    suspend fun deleteProvider(@RequestParam("id") id: Long): ApiResponse<*> {
        return ApiResponse.success(
            withContext(Dispatchers.IO) {
                providerService.getRepository().deleteById(id)
            }
        )
    }
}