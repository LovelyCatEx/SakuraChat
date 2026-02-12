/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.controller.manager

import com.lovelycatv.sakurachat.controller.manager.dto.ManagerCreateAgentDTO
import com.lovelycatv.sakurachat.controller.manager.dto.UpdateAgentDTO
import com.lovelycatv.sakurachat.request.ApiResponse
import com.lovelycatv.sakurachat.request.PageQuery
import com.lovelycatv.sakurachat.service.AgentService
import com.lovelycatv.sakurachat.utils.toPaginatedResponseData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.data.domain.Pageable
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/manager/agent")
@Validated
class ManagerAgentController(
    private val agentService: AgentService
) {
    @GetMapping("/list")
    suspend fun listAgents(@ModelAttribute pageQuery: PageQuery): ApiResponse<*> {
        return ApiResponse.success(
            agentService
                .listByPage(pageQuery.page, pageQuery.pageSize)
                .toPaginatedResponseData()
        )
    }

    @GetMapping("/search")
    suspend fun searchAgents(
        @RequestParam("keyword") keyword: String,
        @ModelAttribute pageQuery: PageQuery
    ): ApiResponse<*> {
        return ApiResponse.success(
            agentService.search(keyword, pageQuery.page, pageQuery.pageSize)
        )
    }

    @PostMapping("/create")
    suspend fun createAgent(@ModelAttribute managerCreateAgentDTO: ManagerCreateAgentDTO): ApiResponse<*> {
        agentService.createAgent(managerCreateAgentDTO)
        return ApiResponse.success(null)
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
