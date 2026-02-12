/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.controller.manager

import com.lovelycatv.sakurachat.controller.manager.dto.ManagerCreateThirdPartyAccountDTO
import com.lovelycatv.sakurachat.controller.manager.dto.UpdateThirdPartyAccountDTO
import com.lovelycatv.sakurachat.request.ApiResponse
import com.lovelycatv.sakurachat.request.PageQuery
import com.lovelycatv.sakurachat.service.ThirdPartyAccountService
import com.lovelycatv.sakurachat.utils.toPaginatedResponseData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/manager/third-party-account")
@Validated
class ManagerThirdPartyAccountController(
    private val thirdPartyAccountService: ThirdPartyAccountService
) {
    @GetMapping("/list")
    suspend fun listThirdPartyAccounts(@ModelAttribute pageQuery: PageQuery): ApiResponse<*> {
        return ApiResponse.success(
            thirdPartyAccountService
                .listByPage(pageQuery.page, pageQuery.pageSize)
                .toPaginatedResponseData()
        )
    }

    @GetMapping("/search")
    suspend fun searchThirdPartyAccounts(
        @RequestParam("keyword") keyword: String,
        @ModelAttribute pageQuery: PageQuery
    ): ApiResponse<*> {
        return ApiResponse.success(
            thirdPartyAccountService.search(keyword, pageQuery.page, pageQuery.pageSize)
        )
    }

    @PostMapping("/create")
    suspend fun createThirdPartyAccount(@ModelAttribute managerCreateThirdPartyAccountDTO: ManagerCreateThirdPartyAccountDTO): ApiResponse<*> {
        thirdPartyAccountService.createThirdPartyAccount(managerCreateThirdPartyAccountDTO)
        return ApiResponse.success(null)
    }

    @PostMapping("/update")
    suspend fun updateThirdPartyAccount(@ModelAttribute updateThirdPartyAccountDTO: UpdateThirdPartyAccountDTO): ApiResponse<*> {
        thirdPartyAccountService.updateThirdPartyAccount(updateThirdPartyAccountDTO)
        return ApiResponse.success(null)
    }

    @PostMapping("/delete")
    suspend fun deleteThirdPartyAccount(@RequestParam("id") id: Long): ApiResponse<*> {
        return ApiResponse.success(
            withContext(Dispatchers.IO) {
                thirdPartyAccountService.getRepository().deleteById(id)
            }
        )
    }
}
