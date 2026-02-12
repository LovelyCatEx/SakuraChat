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
import com.lovelycatv.sakurachat.types.SakuraChatSystemSettings

interface SystemSettingsService : BaseService<SystemSettingsRepository, SystemSettingsEntity, Long> {
    fun getSettings(
        key: String,
        onAbsentOrNull: ((absentOrNull: Boolean) -> String?)? = null
    ): String?

    fun setSettings(key: String, value: String?)

    fun getAllSettings(): SakuraChatSystemSettings

    fun updateAllSettings(settings: SakuraChatSystemSettings)

    fun getAllSettingsLazy(): SakuraChatSystemSettings

    fun refreshSettings()
}