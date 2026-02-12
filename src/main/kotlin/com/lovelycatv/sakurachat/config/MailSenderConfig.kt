/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.config

import com.lovelycatv.sakurachat.constants.SystemSettings
import com.lovelycatv.sakurachat.service.SystemSettingsService
import kotlinx.coroutines.runBlocking
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl

@Configuration
class MailSenderConfig(
    private val systemSettingsService: SystemSettingsService
) {
    @Bean(name = ["sakuraChatMailSender"])
    fun mailSender(): JavaMailSender {
        return runBlocking {
            JavaMailSenderImpl().apply {
                host = systemSettingsService.getSettings(SystemSettings.Mail.SMTP.HOST) { "127.0.0.1" }!!
                port = systemSettingsService.getSettings(SystemSettings.Mail.SMTP.PORT) { "465" }!!.toInt()
                username = systemSettingsService.getSettings(SystemSettings.Mail.SMTP.USERNAME) { "" }!!
                password = systemSettingsService.getSettings(SystemSettings.Mail.SMTP.PASSWORD) { "" }!!
                defaultEncoding = "UTF_8"
            }
        }
    }
}