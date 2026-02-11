/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.service.impl

import com.lovelycatv.sakurachat.controller.manager.dto.UpdateCredentialDTO
import com.lovelycatv.sakurachat.repository.CredentialRepository
import com.lovelycatv.sakurachat.service.CredentialService
import org.springframework.stereotype.Service

@Service
class CredentialServiceImpl(
    private val credentialRepository: CredentialRepository
) : CredentialService {
    override fun getRepository(): CredentialRepository {
        return this.credentialRepository
    }

    override suspend fun updateCredential(updateCredentialDTO: UpdateCredentialDTO) {
        val existing = this.getByIdOrThrow(updateCredentialDTO.id)

        if (updateCredentialDTO.type != null) {
            existing.type = updateCredentialDTO.type
        }

        if (updateCredentialDTO.data != null) {
            existing.data = updateCredentialDTO.data
        }

        if (updateCredentialDTO.active != null) {
            existing.active = updateCredentialDTO.active
        }

        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            getRepository().save(existing)
        }
    }
}