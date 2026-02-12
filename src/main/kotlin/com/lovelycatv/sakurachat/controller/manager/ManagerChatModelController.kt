/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.controller.manager

import com.lovelycatv.sakurachat.controller.manager.dto.ManagerCreateChatModelDTO
import com.lovelycatv.sakurachat.controller.manager.dto.UpdateChatModelDTO
import com.lovelycatv.sakurachat.request.ApiResponse
import com.lovelycatv.sakurachat.request.PageQuery
import com.lovelycatv.sakurachat.service.ChatModelService
import com.lovelycatv.sakurachat.utils.toPaginatedResponseData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/manager/chat-model")
@Validated
class ManagerChatModelController(
    private val chatModelService: ChatModelService
) {
    @GetMapping("/list")
    suspend fun listChatModels(@ModelAttribute pageQuery: PageQuery): ApiResponse<*> {
        return ApiResponse.success(
            chatModelService
                .listByPage(pageQuery.page, pageQuery.pageSize)
                .toPaginatedResponseData()
        )
    }

    @GetMapping("/search")
    suspend fun searchChatModels(
        @RequestParam("keyword") keyword: String,
        @ModelAttribute pageQuery: PageQuery
    ): ApiResponse<*> {
        return ApiResponse.success(chatModelService.search(keyword, pageQuery.page, pageQuery.pageSize))
    }

    @GetMapping("/getById")
    suspend fun getChatModelById(@RequestParam("id") id: Long): ApiResponse<*> {
        return ApiResponse.success(
            chatModelService.getByIdOrThrow(id)
        )
    }

    @PostMapping("/create")
    suspend fun createChatModel(@ModelAttribute createChatModelDTO: ManagerCreateChatModelDTO): ApiResponse<*> {
        chatModelService.createChatModel(createChatModelDTO)
        return ApiResponse.success(null)
    }

    @PostMapping("/update")
    suspend fun updateChatModel(@ModelAttribute updateChatModelDTO: UpdateChatModelDTO): ApiResponse<*> {
        chatModelService.updateChatModel(updateChatModelDTO)
        return ApiResponse.success(null)
    }

    @PostMapping("/delete")
    suspend fun deleteChatModel(@RequestParam("id") id: Long): ApiResponse<*> {
        return ApiResponse.success(
            withContext(Dispatchers.IO) {
                chatModelService.getRepository().deleteById(id)
            }
        )
    }
}
