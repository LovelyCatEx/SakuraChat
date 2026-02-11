/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.service

import com.lovelycatv.sakurachat.controller.manager.dto.ManagerCreateProviderDTO
import com.lovelycatv.sakurachat.controller.manager.dto.UpdateProviderDTO
import com.lovelycatv.sakurachat.entity.ProviderEntity
import com.lovelycatv.sakurachat.repository.ProviderRepository
import com.lovelycatv.sakurachat.request.PaginatedResponseData

interface ProviderService : BaseService<ProviderRepository, ProviderEntity, Long> {
    suspend fun createProvider(managerCreateProviderDTO: ManagerCreateProviderDTO)
    suspend fun updateProvider(updateProviderDTO: UpdateProviderDTO)
    suspend fun search(keyword: String, page: Int, pageSize: Int): PaginatedResponseData<ProviderEntity>
}