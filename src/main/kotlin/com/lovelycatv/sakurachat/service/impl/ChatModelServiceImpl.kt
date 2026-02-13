/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.service.impl

import com.lovelycatv.sakurachat.controller.manager.dto.ManagerCreateChatModelDTO
import com.lovelycatv.sakurachat.controller.manager.dto.ManagerUpdateChatModelDTO
import com.lovelycatv.sakurachat.entity.ChatModelEntity
import com.lovelycatv.sakurachat.entity.aggregated.AggregatedChatModelEntity
import com.lovelycatv.sakurachat.repository.ChatModelRepository
import com.lovelycatv.sakurachat.request.PaginatedResponseData
import com.lovelycatv.sakurachat.service.ChatModelService
import com.lovelycatv.sakurachat.service.CredentialService
import com.lovelycatv.sakurachat.service.ProviderService
import com.lovelycatv.sakurachat.utils.SnowIdGenerator
import com.lovelycatv.sakurachat.utils.toPaginatedResponseData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class ChatModelServiceImpl(
    private val chatModelRepository: ChatModelRepository,
    private val credentialService: CredentialService,
    private val providerService: ProviderService,
    private val snowIdGenerator: SnowIdGenerator
) : ChatModelService {
    override fun getRepository(): ChatModelRepository {
        return this.chatModelRepository
    }

    override fun getAggregatedChatModelEntityById(id: Long): AggregatedChatModelEntity? {
        val chatModel = chatModelRepository.findById(id).orElse(null) ?: return null
        return AggregatedChatModelEntity(
            chatModel = chatModel,
            credential = credentialService
                .getRepository()
                .findById(chatModel.credentialId)
                .orElse(null),
            provider = providerService
                .getRepository()
                .findById(chatModel.providerId)
                .orElse(null)
        )
    }

    override suspend fun updateChatModel(managerUpdateChatModelDTO: ManagerUpdateChatModelDTO) {
        val existing = this.getByIdOrThrow(managerUpdateChatModelDTO.id)

        if (managerUpdateChatModelDTO.name != null) {
            existing.name = managerUpdateChatModelDTO.name
        }

        if (managerUpdateChatModelDTO.description != null) {
            existing.description = managerUpdateChatModelDTO.description
        }

        if (managerUpdateChatModelDTO.providerId != null) {
            existing.providerId = managerUpdateChatModelDTO.providerId
        }

        if (managerUpdateChatModelDTO.qualifiedName != null) {
            existing.qualifiedName = managerUpdateChatModelDTO.qualifiedName
        }

        if (managerUpdateChatModelDTO.maxContextTokens != null) {
            existing.maxContextTokens = managerUpdateChatModelDTO.maxContextTokens
        }

        if (managerUpdateChatModelDTO.temperature != null) {
            existing.temperature = managerUpdateChatModelDTO.temperature
        }

        if (managerUpdateChatModelDTO.maxTokens != null) {
            existing.maxTokens = managerUpdateChatModelDTO.maxTokens
        }

        if (managerUpdateChatModelDTO.inputTokenPointRate != null) {
            existing.inputTokenPointRate = managerUpdateChatModelDTO.inputTokenPointRate
        }

        if (managerUpdateChatModelDTO.outputTokenPointRate != null) {
            existing.outputTokenPointRate = managerUpdateChatModelDTO.outputTokenPointRate
        }

        if (managerUpdateChatModelDTO.cachedInputTokenPointRate != null) {
            existing.cachedInputTokenPointRate = managerUpdateChatModelDTO.cachedInputTokenPointRate
        }

        if (managerUpdateChatModelDTO.credentialId != null) {
            existing.credentialId = managerUpdateChatModelDTO.credentialId
        }

        if (managerUpdateChatModelDTO.active != null) {
            existing.active = managerUpdateChatModelDTO.active
        }

        withContext(Dispatchers.IO) {
            getRepository().save(existing)
        }
    }

    override suspend fun createChatModel(managerCreateChatModelDTO: ManagerCreateChatModelDTO) {
        withContext(Dispatchers.IO) {
            getRepository().save(
                ChatModelEntity(
                    id = snowIdGenerator.nextId(),
                    name = managerCreateChatModelDTO.name,
                    qualifiedName = managerCreateChatModelDTO.qualifiedName,
                    description = managerCreateChatModelDTO.description,
                    providerId = managerCreateChatModelDTO.providerId,
                    credentialId = managerCreateChatModelDTO.credentialId,
                    maxContextTokens = managerCreateChatModelDTO.maxContextTokens,
                    maxTokens = managerCreateChatModelDTO.maxTokens,
                    temperature = managerCreateChatModelDTO.temperature,
                    inputTokenPointRate = managerCreateChatModelDTO.inputTokenPointRate,
                    outputTokenPointRate = managerCreateChatModelDTO.outputTokenPointRate,
                    cachedInputTokenPointRate = managerCreateChatModelDTO.cachedInputTokenPointRate,
                    active = true
                )
            )
        }
    }

    override suspend fun search(keyword: String, page: Int, pageSize: Int): PaginatedResponseData<ChatModelEntity> {
        if (keyword.isBlank()) {
            return this.listByPage(page, pageSize).toPaginatedResponseData()
        }

        return withContext(Dispatchers.IO) {
            getRepository().findAllByNameLikeOrDescriptionLikeOrQualifiedNameLike(
                "%$keyword%",
                "%$keyword%",
                "%$keyword%",
                Pageable.ofSize(5).withPage(0)
            )
        }.toPaginatedResponseData()
    }
}