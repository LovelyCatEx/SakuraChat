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
import com.lovelycatv.sakurachat.entity.thirdparty.ThirdPartyAccountEntity
import com.lovelycatv.sakurachat.repository.AgentThirdPartyAccountRelationRepository
import com.lovelycatv.sakurachat.request.ApiResponse
import com.lovelycatv.sakurachat.request.PageQuery
import com.lovelycatv.sakurachat.service.AgentService
import com.lovelycatv.sakurachat.service.ThirdPartyAccountRelationService
import com.lovelycatv.sakurachat.service.ThirdPartyAccountService
import com.lovelycatv.sakurachat.utils.toPageable
import com.lovelycatv.sakurachat.utils.toPaginatedResponseData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/manager/agent")
@Validated
class ManagerAgentController(
    private val agentService: AgentService,
    private val agentThirdPartyAccountRelationRepository: AgentThirdPartyAccountRelationRepository,
    private val thirdPartyAccountService: ThirdPartyAccountService,
    private val thirdPartyAccountRelationService: ThirdPartyAccountRelationService
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

    @GetMapping("/third-party-account/list")
    fun getAgentThirdPartyAccounts(
        @RequestParam("agentId") agentId: Long,
        @ModelAttribute pageQuery: PageQuery
    ): ApiResponse<*> {
        val accountMap = mutableMapOf<Long, ThirdPartyAccountEntity>()
        return ApiResponse.success(
            agentThirdPartyAccountRelationRepository
                .findByAgentId(
                    agentId,
                    pageQuery.toPageable()
                )
                .also {
                    val account = thirdPartyAccountService
                        .getByIds(it.toList().map { it.primaryKey.thirdPartyAccountId })

                    account.forEach {
                        accountMap[it.id] = it
                    }
                }
                .toPaginatedResponseData {
                    accountMap[it.primaryKey.thirdPartyAccountId]!!
                }
        )
    }

    @GetMapping("/third-party-account/unbound-list")
    fun getUnboundAccountsForAgent(
        @RequestParam("agentId") agentId: Long,
        @ModelAttribute pageQuery: PageQuery
    ): ApiResponse<*> {
        return ApiResponse.success(
            thirdPartyAccountService.getUnboundAccountsForAgent(
                agentId = agentId,
                page = pageQuery.page,
                pageSize = pageQuery.pageSize
            )
        )
    }

    @PostMapping("/third-party-account/bind")
    fun bindAgentThirdPartyAccount(
        @RequestParam("agentId") agentId: Long,
        @RequestParam("id") thirdPartyAccountEntityId: Long
    ): ApiResponse<*> {
        thirdPartyAccountRelationService.bindAgentToThirdPartyAccount(
            agentId = agentId,
            thirdPartyAccountEntityId = thirdPartyAccountEntityId
        )

        return ApiResponse.success(null)
    }

    @PostMapping("/third-party-account/unbind")
    fun unbindAgentThirdPartyAccount(
        @RequestParam("agentId") agentId: Long,
        @RequestParam("id") thirdPartyAccountEntityId: Long
    ): ApiResponse<*> {
        thirdPartyAccountRelationService.unbindAgentToThirdPartyAccount(
            agentId = agentId,
            thirdPartyAccountEntityId = thirdPartyAccountEntityId
        )

        return ApiResponse.success(null)
    }
}
