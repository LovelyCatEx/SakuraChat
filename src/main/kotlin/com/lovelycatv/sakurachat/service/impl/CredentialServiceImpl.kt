/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.service.impl

import com.lovelycatv.sakurachat.controller.manager.dto.ManagerCreateCredentialDTO
import com.lovelycatv.sakurachat.controller.manager.dto.ManagerUpdateCredentialDTO
import com.lovelycatv.sakurachat.entity.CredentialEntity
import com.lovelycatv.sakurachat.repository.CredentialRepository
import com.lovelycatv.sakurachat.request.PaginatedResponseData
import com.lovelycatv.sakurachat.service.CredentialService
import com.lovelycatv.sakurachat.utils.SnowIdGenerator
import com.lovelycatv.sakurachat.utils.toPaginatedResponseData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class CredentialServiceImpl(
    private val credentialRepository: CredentialRepository,
    private val snowIdGenerator: SnowIdGenerator
) : CredentialService {
    override fun getRepository(): CredentialRepository {
        return this.credentialRepository
    }

    override suspend fun updateCredential(managerUpdateCredentialDTO: ManagerUpdateCredentialDTO) {
        val existing = this.getByIdOrThrow(managerUpdateCredentialDTO.id)

        if (managerUpdateCredentialDTO.type != null) {
            existing.type = managerUpdateCredentialDTO.type
        }

        if (managerUpdateCredentialDTO.data != null) {
            existing.data = managerUpdateCredentialDTO.data
        }

        if (managerUpdateCredentialDTO.active != null) {
            existing.active = managerUpdateCredentialDTO.active
        }

        withContext(Dispatchers.IO) {
            getRepository().save(existing)
        }
    }

    override suspend fun createCredential(managerCreateCredentialDTO: ManagerCreateCredentialDTO) {
        withContext(Dispatchers.IO) {
            getRepository().save(
                CredentialEntity(
                    id = snowIdGenerator.nextId(),
                    type = managerCreateCredentialDTO.type,
                    data = managerCreateCredentialDTO.data,
                    active = managerCreateCredentialDTO.active
                )
            )
        }
    }

    override suspend fun search(keyword: String, page: Int, pageSize: Int): PaginatedResponseData<CredentialEntity> {
        if (keyword.isBlank()) {
            return this.listByPage(page, pageSize).toPaginatedResponseData()
        }

        return withContext(Dispatchers.IO) {
            getRepository().findAllByDataContainingIgnoreCase(
                keyword,
                Pageable.ofSize(pageSize).withPage(page - 1)
            )
        }.toPaginatedResponseData()
    }
}