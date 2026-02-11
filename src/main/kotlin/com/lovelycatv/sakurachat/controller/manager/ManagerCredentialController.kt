/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.controller.manager

import com.lovelycatv.sakurachat.controller.manager.dto.ManagerCreateCredentialDTO
import com.lovelycatv.sakurachat.controller.manager.dto.UpdateCredentialDTO
import com.lovelycatv.sakurachat.request.ApiResponse
import com.lovelycatv.sakurachat.request.PageQuery
import com.lovelycatv.sakurachat.service.CredentialService
import com.lovelycatv.sakurachat.utils.toPaginatedResponseData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.data.domain.Pageable
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/manager/credential")
@Validated
class ManagerCredentialController(
    private val credentialService: CredentialService
) {
    @GetMapping("/list")
    suspend fun listCredentials(@ModelAttribute pageQuery: PageQuery): ApiResponse<*> {
        return ApiResponse.success(
            credentialService.getRepository().findAll(
                Pageable
                    .ofSize(pageQuery.pageSize)
                    .withPage(pageQuery.page - 1)
            ).toPaginatedResponseData()
        )
    }

    @GetMapping("/search")
    suspend fun searchCredentials(@RequestParam("keyword") keyword: String): ApiResponse<*> {
        return ApiResponse.success(
            credentialService.getRepository().findAll().filter {
                keyword.isBlank() ||
                it.data.contains(keyword, ignoreCase = true)
            }
        )
    }

    @GetMapping("/getById")
    suspend fun getCredentialById(@RequestParam("id") id: Long): ApiResponse<*> {
        return ApiResponse.success(
            credentialService.getRepository().findById(id).orElse(null)
        )
    }

    @PostMapping("/create")
    suspend fun createCredential(@ModelAttribute managerCreateCredentialDTO: ManagerCreateCredentialDTO): ApiResponse<*> {
        credentialService.createCredential(managerCreateCredentialDTO)
        return ApiResponse.success(null)
    }

    @PostMapping("/update")
    suspend fun updateCredential(@ModelAttribute updateCredentialDTO: UpdateCredentialDTO): ApiResponse<*> {
        credentialService.updateCredential(updateCredentialDTO)
        return ApiResponse.success(null)
    }

    @PostMapping("/delete")
    suspend fun deleteCredential(@RequestParam("id") id: Long): ApiResponse<*> {
        return ApiResponse.success(
            withContext(Dispatchers.IO) {
                credentialService.getRepository().deleteById(id)
            }
        )
    }
}
