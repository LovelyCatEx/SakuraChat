/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.service.mail

import com.lovelycatv.sakurachat.service.SystemSettingsService
import com.lovelycatv.sakurachat.types.SakuraChatSystemSettings
import com.lovelycatv.vertex.log.logger
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.stereotype.Component
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

@Component
class JavaMailSenderContainer(
    private val systemSettingsService: SystemSettingsService,
) {
    private val logger = logger()

    private var mailSender: JavaMailSender? = null
    private var needRefresh: Boolean = true
    private val mutex = ReentrantLock()

    fun getInstance(): JavaMailSender {
        return mutex.withLock {
            if (this.mailSender == null || needRefresh) {
                val settings = systemSettingsService.getAllSettingsLazy()
                logger.info("MailSenderContainer is refreshing, settings: ${settings.mail}")
                this.mailSender = createMailSender(settings)
                this.needRefresh = false
            }

            this.mailSender!!
        }
    }

    fun refreshSettings() {
        this.needRefresh = true
    }

    fun createMailSender(sakuraChatSystemSettings: SakuraChatSystemSettings): JavaMailSender {
        return JavaMailSenderImpl().apply {
            host = sakuraChatSystemSettings.mail.smtp.host
            port = sakuraChatSystemSettings.mail.smtp.port
            username = sakuraChatSystemSettings.mail.smtp.username
            password = sakuraChatSystemSettings.mail.smtp.password
            defaultEncoding = "UTF_8"
            javaMailProperties = Properties().apply {
                val timeout = 5000
                this["mail.transport.protocol"] = "smtp"
                this["mail.smtp.auth"] = "true"
                this["mail.smtp.ssl.enable"] = sakuraChatSystemSettings.mail.smtp.ssl.toString()
                this["mail.smtp.starttls.required"] = "true"
                this["mail.smtp.ssl.trust"] = "*"
                this["mail.smtp.connectiontimeout"] = "$timeout"
                this["mail.smtp.timeout"] = "$timeout"
                this["mail.smtp.writetimeout"] = "$timeout"
                this["mail.smtp.ssl.connectiontimeout"] = "$timeout"
                this["mail.smtp.ssl.timeout"] = "$timeout"
            }
        }
    }

    fun getSystemSettings(): SakuraChatSystemSettings {
        return this.systemSettingsService.getAllSettingsLazy()
            ?: throw IllegalStateException("container has not been initialized, please call getInstance() first")
    }
}