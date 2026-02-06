/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.service.impl

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
}