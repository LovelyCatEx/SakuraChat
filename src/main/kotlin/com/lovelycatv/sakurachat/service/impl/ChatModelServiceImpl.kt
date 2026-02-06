/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.service.impl

import com.lovelycatv.sakurachat.entity.aggregated.AggregatedChatModelEntity
import com.lovelycatv.sakurachat.repository.ChatModelRepository
import com.lovelycatv.sakurachat.service.ChatModelService
import com.lovelycatv.sakurachat.service.CredentialService
import com.lovelycatv.sakurachat.service.ProviderService
import org.springframework.stereotype.Service

@Service
class ChatModelServiceImpl(
    private val chatModelRepository: ChatModelRepository,
    private val credentialService: CredentialService,
    private val providerService: ProviderService
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
}