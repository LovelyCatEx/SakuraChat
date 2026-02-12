/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.controller.manager

import com.lovelycatv.sakurachat.request.ApiResponse
import com.lovelycatv.sakurachat.service.SystemSettingsService
import com.lovelycatv.sakurachat.service.mail.MailService
import com.lovelycatv.sakurachat.types.SakuraChatSystemSettings
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/manager/settings")
class ManagerSettingsController(
    private val systemSettingsService: SystemSettingsService,
    private val mailService: MailService,
) {
    @GetMapping("/list")
    suspend fun getAllSystemSettings(): ApiResponse<*> {
        return ApiResponse.success(systemSettingsService.getAllSettings())
    }

    @PostMapping("/update")
    suspend fun updateSettings(@RequestBody settings: SakuraChatSystemSettings): ApiResponse<*> {
        return ApiResponse.success(systemSettingsService.updateAllSettings(settings))
    }

    @PostMapping("/sendTestEmail")
    suspend fun sendTestEmail(@RequestParam("email") email: String): ApiResponse<*> {
        mailService.refreshSettings()
        mailService.sendMail(email, "SakuraChat", "SakuraChat TestEmail")

        return ApiResponse.success(email)
    }
}