/*
 * Copyright 2025 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */
package com.lovelycatv.sakurachat.controller

import com.lovelycatv.sakurachat.controller.dto.UserRegisterDTO
import com.lovelycatv.sakurachat.response.ApiResponse
import com.lovelycatv.sakurachat.service.UserService
import jakarta.annotation.Resource
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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
}