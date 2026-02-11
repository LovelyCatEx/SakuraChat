/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.service

import com.lovelycatv.sakurachat.controller.manager.dto.ManagerCreateCredentialDTO
import com.lovelycatv.sakurachat.controller.manager.dto.UpdateCredentialDTO
import com.lovelycatv.sakurachat.entity.CredentialEntity
import com.lovelycatv.sakurachat.repository.CredentialRepository

interface CredentialService : BaseService<CredentialRepository, CredentialEntity, Long> {
    suspend fun updateCredential(updateCredentialDTO: UpdateCredentialDTO)

    suspend fun createCredential(managerCreateCredentialDTO: ManagerCreateCredentialDTO)
}