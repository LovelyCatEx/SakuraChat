/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */
package com.lovelycatv.sakurachat.controller.user

import com.lovelycatv.sakurachat.annotations.Unauthorized
import com.lovelycatv.sakurachat.constants.SystemRolePermissions
import com.lovelycatv.sakurachat.controller.user.dto.UserProfileVO
import com.lovelycatv.sakurachat.controller.user.dto.UserRegisterDTO
import com.lovelycatv.sakurachat.request.ApiResponse
import com.lovelycatv.sakurachat.service.UserPointsService
import com.lovelycatv.sakurachat.service.user.UserService
import com.lovelycatv.sakurachat.types.UserAuthentication
import jakarta.annotation.Resource
import jakarta.validation.constraints.Email
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
@Validated
class UserController {
    @Autowired
    private lateinit var userPointsService: UserPointsService

    @Resource
    private lateinit var userService: UserService

    @Unauthorized
    @PostMapping("/register")
    suspend fun register(dto: UserRegisterDTO): ApiResponse<*> {
        userService.register(dto.username, dto.password, dto.email, dto.emailCode)

        return ApiResponse.success(null, dto.email)
    }

    @Unauthorized
    @PostMapping("/requestRegisterEmailCode")
    suspend fun requestRegisterEmailCode(
        @RequestParam("email")
        @Email(message = "Must be a valid email address")
        email: String
    ): ApiResponse<*> {
        userService.sendRegisterEmail(email)

        return ApiResponse.success(null)
    }

    @Unauthorized
    @PostMapping("/requestPasswordResetEmailCode")
    suspend fun requestPasswordResetEmailCode(
        @RequestParam("email")
        @Email(message = "Must be a valid email address")
        email: String
    ): ApiResponse<*> {
        userService.sendPasswordResetEmail(email)

        return ApiResponse.success(null)
    }

    @Unauthorized
    @PostMapping("/resetPassword")
    suspend fun resetPassword(
        @RequestParam("email")
        @Email(message = "Must be a valid email address")
        email: String,
        @RequestParam("emailCode")
        emailCode: String,
        @RequestParam("newPassword")
        newPassword: String
    ): ApiResponse<*> {
        userService.resetPassword(email, emailCode, newPassword)

        return ApiResponse.success(null)
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
                userId = user.id,
                username = user.username,
                nickname = user.nickname,
                email = if (isSelf) user.email else null,
            )
        )
    }

    @PreAuthorize(SystemRolePermissions.PERMISSION_PUBLIC)
    @GetMapping("/points")
    suspend fun getUserPoints(userAuthentication: UserAuthentication): ApiResponse<*> {
        return ApiResponse.success(
            userPointsService.getUserPoints(userAuthentication.userId)
        )
    }
}