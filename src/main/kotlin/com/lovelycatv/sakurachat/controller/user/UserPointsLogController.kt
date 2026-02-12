/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.controller.user

import com.lovelycatv.sakurachat.request.ApiResponse
import com.lovelycatv.sakurachat.request.PageQuery
import com.lovelycatv.sakurachat.service.UserPointsLogService
import com.lovelycatv.sakurachat.types.UserAuthentication
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/point")
@Validated
class UserPointsLogController(
    private val userPointsLogService: UserPointsLogService
) {
    @GetMapping("/logs")
    suspend fun getUserPointsLogs(
        userAuthentication: UserAuthentication,
        @ModelAttribute pageQuery: PageQuery
    ): ApiResponse<*> {
        return ApiResponse.success(
            userPointsLogService.listUserPointsLogs(userAuthentication.userId, pageQuery)
        )
    }
}