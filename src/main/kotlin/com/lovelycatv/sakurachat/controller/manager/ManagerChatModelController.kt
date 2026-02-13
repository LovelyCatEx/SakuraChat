/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.controller.manager

import com.lovelycatv.sakurachat.constants.SystemRolePermissions
import com.lovelycatv.sakurachat.controller.manager.dto.ManagerCreateChatModelDTO
import com.lovelycatv.sakurachat.controller.manager.dto.ManagerUpdateChatModelDTO
import com.lovelycatv.sakurachat.request.ApiResponse
import com.lovelycatv.sakurachat.request.PageQuery
import com.lovelycatv.sakurachat.service.ChatModelService
import com.lovelycatv.sakurachat.utils.toPaginatedResponseData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/manager/chat-model")
@Validated
class ManagerChatModelController(
    private val chatModelService: ChatModelService
) {
    @PreAuthorize(SystemRolePermissions.PERMISSION_ROOT_ADMIN)
    @GetMapping("/list")
    suspend fun listChatModels(@ModelAttribute pageQuery: PageQuery): ApiResponse<*> {
        return ApiResponse.success(
            chatModelService
                .listByPage(pageQuery.page, pageQuery.pageSize)
                .toPaginatedResponseData()
        )
    }

    @PreAuthorize(SystemRolePermissions.PERMISSION_ROOT_ADMIN)
    @GetMapping("/search")
    suspend fun searchChatModels(
        @RequestParam("keyword") keyword: String,
        @ModelAttribute pageQuery: PageQuery
    ): ApiResponse<*> {
        return ApiResponse.success(chatModelService.search(keyword, pageQuery.page, pageQuery.pageSize))
    }

    @PreAuthorize(SystemRolePermissions.PERMISSION_ROOT_ADMIN)
    @GetMapping("/getById")
    suspend fun getChatModelById(@RequestParam("id") id: Long): ApiResponse<*> {
        return ApiResponse.success(
            chatModelService.getByIdOrThrow(id)
        )
    }

    @PreAuthorize(SystemRolePermissions.PERMISSION_ROOT_ADMIN)
    @PostMapping("/create")
    suspend fun createChatModel(@ModelAttribute createChatModelDTO: ManagerCreateChatModelDTO): ApiResponse<*> {
        chatModelService.createChatModel(createChatModelDTO)
        return ApiResponse.success(null)
    }

    @PreAuthorize(SystemRolePermissions.PERMISSION_ROOT_ADMIN)
    @PostMapping("/update")
    suspend fun updateChatModel(@ModelAttribute managerUpdateChatModelDTO: ManagerUpdateChatModelDTO): ApiResponse<*> {
        chatModelService.updateChatModel(managerUpdateChatModelDTO)
        return ApiResponse.success(null)
    }

    @PreAuthorize(SystemRolePermissions.PERMISSION_ROOT_ADMIN)
    @PostMapping("/delete")
    suspend fun deleteChatModel(@RequestParam("id") id: Long): ApiResponse<*> {
        return ApiResponse.success(
            withContext(Dispatchers.IO) {
                chatModelService.getRepository().deleteById(id)
            }
        )
    }
}
