/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.controller.manager

import com.lovelycatv.sakurachat.constants.SystemRolePermissions
import com.lovelycatv.sakurachat.request.ApiResponse
import com.lovelycatv.sakurachat.repository.*
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/manager/dashboard")
class ManagerDashboardController(
    private val userRepository: UserRepository,
    private val providerRepository: ProviderRepository,
    private val chatModelRepository: ChatModelRepository,
    private val thirdPartyAccountRepository: ThirdPartyAccountRepository,
    private val userPointsLogRepository: UserPointsLogRepository
) {
    data class DashboardStats(
        val totalUsers: Long,
        val totalProviders: Long,
        val totalModels: Long,
        val totalThirdPartyAccounts: Long,
        val totalPointsConsumed: Long
    )

    @PreAuthorize(SystemRolePermissions.PERMISSION_ROOT_ADMIN)
    @GetMapping("/stats")
    fun getDashboardStats(): ApiResponse<DashboardStats> {
        val totalUsers = userRepository.count()
        val totalProviders = providerRepository.count()
        val totalModels = chatModelRepository.count()
        val totalThirdPartyAccounts = thirdPartyAccountRepository.count()
        val totalPointsConsumed = userPointsLogRepository.getTotalPointsConsumed()

        return ApiResponse.success(
            DashboardStats(
                totalUsers = totalUsers,
                totalProviders = totalProviders,
                totalModels = totalModels,
                totalThirdPartyAccounts = totalThirdPartyAccounts,
                totalPointsConsumed = totalPointsConsumed
            )
        )
    }
}
