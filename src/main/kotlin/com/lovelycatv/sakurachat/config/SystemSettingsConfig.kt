/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.config

import com.lovelycatv.sakurachat.service.SystemSettingsService
import com.lovelycatv.sakurachat.types.SakuraChatSystemSettings
import kotlinx.coroutines.runBlocking
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SystemSettingsConfig(
    private val systemSettingsService: SystemSettingsService
) {
    @Bean
    fun sakuraChatSystemSettings(): SakuraChatSystemSettings {
        return runBlocking {
            systemSettingsService.getAllSettings()
        }
    }
}