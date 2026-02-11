/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.controller.manager

import com.lovelycatv.sakurachat.controller.manager.dto.UpdateAgentDTO
import com.lovelycatv.sakurachat.request.ApiResponse
import com.lovelycatv.sakurachat.request.PageQuery
import com.lovelycatv.sakurachat.service.AgentService
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
@RequestMapping("/manager/agent")
@Validated
class ManagerAgentController(
    private val agentService: AgentService
) {
    @GetMapping("/list")
    suspend fun listAgents(@ModelAttribute pageQuery: PageQuery): ApiResponse<*> {
        return ApiResponse.success(
            agentService.getRepository().findAll(
                Pageable
                    .ofSize(pageQuery.pageSize)
                    .withPage(pageQuery.page - 1)
            ).toPaginatedResponseData()
        )
    }

    @PostMapping("/update")
    suspend fun updateAgent(@ModelAttribute updateAgentDTO: UpdateAgentDTO): ApiResponse<*> {
        agentService.updateAgent(updateAgentDTO)
        return ApiResponse.success(null)
    }

    @PostMapping("/delete")
    suspend fun deleteAgent(@RequestParam("id") id: Long): ApiResponse<*> {
        return ApiResponse.success(
            withContext(Dispatchers.IO) {
                agentService.getRepository().deleteById(id)
            }
        )
    }
}
