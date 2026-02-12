/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.service.impl

import com.lovelycatv.sakurachat.constants.SystemSettings
import com.lovelycatv.sakurachat.entity.SystemSettingsEntity
import com.lovelycatv.sakurachat.repository.SystemSettingsRepository
import com.lovelycatv.sakurachat.service.SystemSettingsService
import com.lovelycatv.sakurachat.types.SakuraChatSystemSettings
import com.lovelycatv.sakurachat.utils.SnowIdGenerator
import com.lovelycatv.vertex.log.logger
import org.springframework.stereotype.Service

@Service
class SystemSettingsServiceImpl(
    private val systemSettingsRepository: SystemSettingsRepository,
    private val snowIdGenerator: SnowIdGenerator
) : SystemSettingsService {
    private val logger = logger()

    override fun getRepository(): SystemSettingsRepository {
        return this.systemSettingsRepository
    }

    override fun getSettings(
        key: String,
        onAbsentOrNull: ((absentOrNull: Boolean) -> String?)?
    ): String? {
        val settings = getRepository().findByKey(key).getOrNull(0)

        if (settings != null) {
            if (settings.value == null && onAbsentOrNull != null) {
                val newValue = onAbsentOrNull.invoke(false)

                this.setSettings(key, newValue)

                logger.info("Changed system settings [$key] to [$newValue] caused by original null value")

                return newValue
            } else {
                return settings.value
            }
        }

        if (onAbsentOrNull == null) {
            return null
        }

        val absentValue = onAbsentOrNull.invoke(true)

        this.setSettings(key, absentValue)

        logger.info("Set system settings [$key] to [$absentValue] caused by absent settings key")

        return absentValue
    }

    override fun setSettings(key: String, value: String?) {
        val existingSettings = getRepository().findByKey(key).getOrNull(0)

        (if (existingSettings != null) {
            getRepository().save(
                existingSettings.apply {
                    this.value = value
                }
            )
        } else {
            getRepository().save(
                SystemSettingsEntity(
                    id = snowIdGenerator.nextId(),
                    key = key,
                    value = value
                )
            )
        }).also {
            logger.info("System settings [$key] successfully set to [$value]")
        }
    }

    override fun getAllSettings(): SakuraChatSystemSettings{
        return SakuraChatSystemSettings(
            userRegistration = SakuraChatSystemSettings.UserRegistration(
                initialPoints = getSettings(SystemSettings.UserRegistration.INITIAL_POINTS) { "0" }!!.toLong()
            ),
            mail = SakuraChatSystemSettings.Mail(
                smtp = SakuraChatSystemSettings.Mail.SMTP(
                    host = getSettings(SystemSettings.Mail.SMTP.HOST) { "127.0.0.1" } !!,
                    port = getSettings(SystemSettings.Mail.SMTP.PORT) { "465" }!!.toInt(),
                    username = getSettings(SystemSettings.Mail.SMTP.USERNAME) { "" }!!,
                    password = getSettings(SystemSettings.Mail.SMTP.PASSWORD) { "" }!!,
                    ssl = getSettings(SystemSettings.Mail.SMTP.SSL) { "true" }!!.toBoolean(),
                    fromEmail = getSettings(SystemSettings.Mail.SMTP.FROM_EMAIL) { "user@example.com" }!!,
                )
            )
        )
    }

    override fun updateAllSettings(settings: SakuraChatSystemSettings) {
        setSettings(SystemSettings.UserRegistration.INITIAL_POINTS, settings.userRegistration.initialPoints.toString())

        setSettings(SystemSettings.Mail.SMTP.HOST, settings.mail.smtp.host)
        setSettings(SystemSettings.Mail.SMTP.PORT, settings.mail.smtp.port.toString())
        setSettings(SystemSettings.Mail.SMTP.USERNAME, settings.mail.smtp.username)
        setSettings(SystemSettings.Mail.SMTP.PASSWORD, settings.mail.smtp.password)
        setSettings(SystemSettings.Mail.SMTP.SSL, settings.mail.smtp.ssl.toString())
        setSettings(SystemSettings.Mail.SMTP.FROM_EMAIL, settings.mail.smtp.fromEmail)

        this.refreshSettings()
    }

    private var sakuraChatSystemSettings: SakuraChatSystemSettings? = null
    private var needRefreshSettings = true
    override fun getAllSettingsLazy(): SakuraChatSystemSettings {
        if (sakuraChatSystemSettings == null || needRefreshSettings) {
            this.sakuraChatSystemSettings = this.getAllSettings()
        }

        return this.sakuraChatSystemSettings!!
    }

    override fun refreshSettings() {
        this.needRefreshSettings = true
    }
}