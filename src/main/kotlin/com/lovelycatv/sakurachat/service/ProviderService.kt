/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.service

import com.lovelycatv.sakurachat.controller.manager.dto.UpdateProviderDTO
import com.lovelycatv.sakurachat.entity.ProviderEntity
import com.lovelycatv.sakurachat.repository.ProviderRepository

interface ProviderService : BaseService<ProviderRepository, ProviderEntity, Long> {
    suspend fun updateProvider(updateProviderDTO: UpdateProviderDTO)
}