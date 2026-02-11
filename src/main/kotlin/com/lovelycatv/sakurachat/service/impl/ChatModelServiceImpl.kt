/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.service.impl

import com.lovelycatv.sakurachat.controller.manager.dto.CreateChatModelDTO
import com.lovelycatv.sakurachat.controller.manager.dto.UpdateChatModelDTO
import com.lovelycatv.sakurachat.entity.ChatModelEntity
import com.lovelycatv.sakurachat.entity.aggregated.AggregatedChatModelEntity
import com.lovelycatv.sakurachat.repository.ChatModelRepository
import com.lovelycatv.sakurachat.service.ChatModelService
import com.lovelycatv.sakurachat.service.CredentialService
import com.lovelycatv.sakurachat.service.ProviderService
import com.lovelycatv.sakurachat.utils.SnowIdGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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

    override suspend fun updateChatModel(updateChatModelDTO: UpdateChatModelDTO) {
        val existing = this.getByIdOrThrow(updateChatModelDTO.id)

        if (updateChatModelDTO.name != null) {
            existing.name = updateChatModelDTO.name
        }

        if (updateChatModelDTO.description != null) {
            existing.description = updateChatModelDTO.description
        }

        if (updateChatModelDTO.providerId != null) {
            existing.providerId = updateChatModelDTO.providerId
        }

        if (updateChatModelDTO.qualifiedName != null) {
            existing.qualifiedName = updateChatModelDTO.qualifiedName
        }

        if (updateChatModelDTO.maxContextTokens != null) {
            existing.maxContextTokens = updateChatModelDTO.maxContextTokens
        }

        if (updateChatModelDTO.temperature != null) {
            existing.temperature = updateChatModelDTO.temperature
        }

        if (updateChatModelDTO.maxTokens != null) {
            existing.maxTokens = updateChatModelDTO.maxTokens
        }

        if (updateChatModelDTO.inputTokenPointRate != null) {
            existing.inputTokenPointRate = updateChatModelDTO.inputTokenPointRate
        }

        if (updateChatModelDTO.outputTokenPointRate != null) {
            existing.outputTokenPointRate = updateChatModelDTO.outputTokenPointRate
        }

        if (updateChatModelDTO.cachedInputTokenPointRate != null) {
            existing.cachedInputTokenPointRate = updateChatModelDTO.cachedInputTokenPointRate
        }

        if (updateChatModelDTO.credentialId != null) {
            existing.credentialId = updateChatModelDTO.credentialId
        }

        if (updateChatModelDTO.active != null) {
            existing.active = updateChatModelDTO.active
        }

        withContext(Dispatchers.IO) {
            getRepository().save(existing)
        }
    }

    override suspend fun createChatModel(createChatModelDTO: CreateChatModelDTO) {
        withContext(Dispatchers.IO) {
            getRepository().save(
                ChatModelEntity(
                    id = snowIdGenerator.nextId(),
                    name = createChatModelDTO.name,
                    qualifiedName = createChatModelDTO.qualifiedName,
                    description = createChatModelDTO.description,
                    providerId = createChatModelDTO.providerId,
                    credentialId = createChatModelDTO.credentialId,
                    maxContextTokens = createChatModelDTO.maxContextTokens,
                    maxTokens = createChatModelDTO.maxTokens,
                    temperature = createChatModelDTO.temperature,
                    inputTokenPointRate = createChatModelDTO.inputTokenPointRate,
                    outputTokenPointRate = createChatModelDTO.outputTokenPointRate,
                    cachedInputTokenPointRate = createChatModelDTO.cachedInputTokenPointRate,
                    active = true
                )
            )
        }
    }
}