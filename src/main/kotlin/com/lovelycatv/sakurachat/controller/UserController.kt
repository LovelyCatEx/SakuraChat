/*
 * Copyright 2025 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */
package com.lovelycatv.sakurachat.controller

import com.lovelycatv.sakurachat.annotations.Unauthorized
import com.lovelycatv.sakurachat.controller.dto.UserProfileVO
import com.lovelycatv.sakurachat.controller.dto.UserRegisterDTO
import com.lovelycatv.sakurachat.response.ApiResponse
import com.lovelycatv.sakurachat.service.UserService
import com.lovelycatv.sakurachat.types.UserAuthentication
import jakarta.annotation.Resource
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
class UserController {
    @Resource
    private lateinit var userService: UserService

    @PostMapping("/register")
    suspend fun register(dto: UserRegisterDTO): ApiResponse<*> {
        userService.register(dto.username, dto.password, dto.email, dto.emailCode)

        return ApiResponse.success(null, "")
    }

    @Unauthorized
    @GetMapping("/profile")
    suspend fun getUserProfile(
        userAuthentication: UserAuthentication?,
        @RequestParam(required = false, defaultValue = "0") userId: Long?
    ): ApiResponse<*> {
        if (userAuthentication == null && (userId == null || userId == 0L)) {
            return ApiResponse.unauthorized<Nothing>("please provide a valid token or user id")
        }

        val validUserId = if (userId != null && userId > 0)
            userId
        else
            userAuthentication!!.userId

        val user = userService.getUserProfileById(validUserId)

        val isSelf = validUserId == user.id

        return ApiResponse.success(
            UserProfileVO(
                userId = user.id!!,
                username = user.username,
                nickname = user.nickname,
                email = if (isSelf) user.email else null,
            )
        )
    }
}