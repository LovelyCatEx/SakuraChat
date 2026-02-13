/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.service.impl

import com.lovelycatv.sakurachat.controller.manager.dto.ManagerCreateProviderDTO
import com.lovelycatv.sakurachat.controller.manager.dto.ManagerUpdateProviderDTO
import com.lovelycatv.sakurachat.entity.ProviderEntity
import com.lovelycatv.sakurachat.repository.ProviderRepository
import com.lovelycatv.sakurachat.request.PaginatedResponseData
import com.lovelycatv.sakurachat.service.ProviderService
import com.lovelycatv.sakurachat.utils.SnowIdGenerator
import com.lovelycatv.sakurachat.utils.toPaginatedResponseData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class ProviderServiceImpl(
    private val providerRepository: ProviderRepository,
    private val snowIdGenerator: SnowIdGenerator,
) : ProviderService {
    override fun getRepository(): ProviderRepository {
        return this.providerRepository
    }

    override suspend fun createProvider(managerCreateProviderDTO: ManagerCreateProviderDTO) {
        val provider = ProviderEntity(
            id = snowIdGenerator.nextId(),
            name = managerCreateProviderDTO.name,
            description = managerCreateProviderDTO.description,
            apiType = managerCreateProviderDTO.apiType,
            chatCompletionsUrl = managerCreateProviderDTO.chatCompletionsUrl
        )

        withContext(Dispatchers.IO) {
            getRepository().save(provider)
        }
    }

    override suspend fun updateProvider(managerUpdateProviderDTO: ManagerUpdateProviderDTO) {
        val existing = this.getByIdOrThrow(managerUpdateProviderDTO.id)

        if (managerUpdateProviderDTO.name != null) {
            existing.name = managerUpdateProviderDTO.name
        }

        if (managerUpdateProviderDTO.description != null) {
            existing.description = managerUpdateProviderDTO.description
        }

        if (managerUpdateProviderDTO.chatCompletionsUrl != null) {
            existing.chatCompletionsUrl = managerUpdateProviderDTO.chatCompletionsUrl
        }

        if (managerUpdateProviderDTO.apiType != null) {
            existing.apiType = managerUpdateProviderDTO.apiType
        }

        withContext(Dispatchers.IO) {
            getRepository().save(existing)
        }
    }

    override suspend fun search(keyword: String, page: Int, pageSize: Int): PaginatedResponseData<ProviderEntity> {
        if (keyword.isBlank()) {
            return this.listByPage(page, pageSize).toPaginatedResponseData()
        }

        return withContext(Dispatchers.IO) {
            getRepository().findAllByNameContainingIgnoreCaseOrChatCompletionsUrlContainingIgnoreCase(
                keyword,
                keyword,
                Pageable.ofSize(pageSize).withPage(page - 1)
            )
        }.toPaginatedResponseData()
    }
}