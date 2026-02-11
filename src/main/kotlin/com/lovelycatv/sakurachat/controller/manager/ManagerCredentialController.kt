/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.controller.manager

import com.lovelycatv.sakurachat.controller.manager.dto.UpdateCredentialDTO
import com.lovelycatv.sakurachat.request.ApiResponse
import com.lovelycatv.sakurachat.request.PageQuery
import com.lovelycatv.sakurachat.service.CredentialService
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
