/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.service.impl

import com.lovelycatv.sakurachat.repository.ProviderRepository
import com.lovelycatv.sakurachat.service.ProviderService
import org.springframework.stereotype.Service

@Service
class ProviderServiceImpl(
    private val providerRepository: ProviderRepository,
) : ProviderService {
    override fun getRepository(): ProviderRepository {
        return this.providerRepository
    }
}