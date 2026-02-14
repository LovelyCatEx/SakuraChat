/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.controller.user

import com.lovelycatv.sakurachat.constants.SystemRolePermissions
import com.lovelycatv.sakurachat.exception.BusinessException
import com.lovelycatv.sakurachat.request.ApiResponse
import com.lovelycatv.sakurachat.service.PointsCdKeyService
import com.lovelycatv.sakurachat.service.UserPointsService
import com.lovelycatv.sakurachat.service.request.UserPointsConsumeRequest
import com.lovelycatv.sakurachat.types.DatabaseTableType
import com.lovelycatv.sakurachat.types.PointsChangesReason
import com.lovelycatv.sakurachat.types.UserAuthentication
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/cdkey")
@Validated
class UserCdKeyController(
    private val pointsCdKeyService: PointsCdKeyService
) {
    @PreAuthorize(SystemRolePermissions.PERMISSION_PUBLIC)
    @PostMapping("/redeem")
    suspend fun redeemCdKey(
        userAuthentication: UserAuthentication,
        @ModelAttribute request: RedeemCdKeyRequest
    ): ApiResponse<*> {
        val points = pointsCdKeyService.redeemCdKey(userAuthentication.userId, request.cdKey)

        return ApiResponse.success(
            mapOf(
                "points" to points,
                "message" to "redeemed successfully"
            )
        )
    }

    data class RedeemCdKeyRequest(
        val cdKey: String
    )
}
