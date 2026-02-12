/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.service.impl

import com.lovelycatv.sakurachat.entity.SystemSettingsEntity
import com.lovelycatv.sakurachat.repository.SystemSettingsRepository
import com.lovelycatv.sakurachat.service.SystemSettingsService
import com.lovelycatv.sakurachat.utils.SnowIdGenerator
import com.lovelycatv.vertex.log.logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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

    override suspend fun getSettings(
        key: String,
        onAbsentOrNull: ((absentOrNull: Boolean) -> String?)?
    ): String? {
        val settings = withContext(Dispatchers.IO) {
            getRepository().findByKey(key)
        }.getOrNull(0)

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

    override suspend fun setSettings(key: String, value: String?) {
        withContext(Dispatchers.IO) {
            getRepository().save(
                SystemSettingsEntity(
                    id = snowIdGenerator.nextId(),
                    key = key,
                    value = value
                )
            ).also {
                logger.info("System settings [$key] successfully set to [$value]")
            }
        }
    }
}