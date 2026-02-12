/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.service

import com.lovelycatv.sakurachat.entity.SystemSettingsEntity
import com.lovelycatv.sakurachat.repository.SystemSettingsRepository

interface SystemSettingsService : BaseService<SystemSettingsRepository, SystemSettingsEntity, Long> {
    suspend fun getSettings(
        key: String,
        onAbsentOrNull: ((absentOrNull: Boolean) -> String?)? = null
    ): String?

    suspend fun setSettings(key: String, value: String?)
}