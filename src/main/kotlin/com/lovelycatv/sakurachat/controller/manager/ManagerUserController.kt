/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.controller.manager

import com.lovelycatv.sakurachat.controller.manager.dto.ManagerCreateUserDTO
import com.lovelycatv.sakurachat.controller.manager.dto.UpdateUserDTO
import com.lovelycatv.sakurachat.request.ApiResponse
import com.lovelycatv.sakurachat.request.PageQuery
import com.lovelycatv.sakurachat.service.user.UserService
import com.lovelycatv.sakurachat.utils.toPaginatedResponseData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.data.domain.Pageable
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/manager/user")
@Validated
class ManagerUserController(
    private val userService: UserService
) {
    @GetMapping("/list")
    suspend fun listUsers(@ModelAttribute pageQuery: PageQuery): ApiResponse<*> {
        return ApiResponse.success(
            userService
                .listByPage(pageQuery.page, pageQuery.pageSize)
                .toPaginatedResponseData()
        )
    }

    @GetMapping("/search")
    suspend fun searchUsers(
        @RequestParam("keyword") keyword: String,
        @ModelAttribute pageQuery: PageQuery
    ): ApiResponse<*> {
        return ApiResponse.success(
            userService.search(keyword, pageQuery.page, pageQuery.pageSize)
        )
    }

    @GetMapping("/getById")
    suspend fun getUserById(@RequestParam("id") id: Long): ApiResponse<*> {
        return ApiResponse.success(
            userService.getRepository().findById(id).orElse(null)
        )
    }

    @PostMapping("/create")
    suspend fun createUser(@ModelAttribute managerCreateUserDTO: ManagerCreateUserDTO): ApiResponse<*> {
        userService.createUser(managerCreateUserDTO)
        return ApiResponse.success(null)
    }

    @PostMapping("/update")
    suspend fun updateUser(@ModelAttribute updateUserDTO: UpdateUserDTO): ApiResponse<*> {
        userService.updateUser(updateUserDTO)
        return ApiResponse.success(null)
    }

    @PostMapping("/delete")
    suspend fun deleteUser(@RequestParam("id") id: Long): ApiResponse<*> {
        return ApiResponse.success(
            withContext(Dispatchers.IO) {
                userService.getRepository().deleteById(id)
            }
        )
    }
}
