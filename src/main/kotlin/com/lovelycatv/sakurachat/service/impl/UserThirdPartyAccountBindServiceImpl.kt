/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.service.impl

import com.lovelycatv.sakurachat.exception.BusinessException
import com.lovelycatv.sakurachat.repository.UserThirdPartyAccountRelationRepository
import com.lovelycatv.sakurachat.service.ThirdPartyAccountRelationService
import com.lovelycatv.sakurachat.service.UserThirdPartyAccountBindService
import com.lovelycatv.vertex.cache.store.ExpiringKVStore
import com.lovelycatv.vertex.cache.store.InMemoryKVStore
import com.lovelycatv.vertex.log.logger
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserThirdPartyAccountBindServiceImpl(
    private val userThirdPartyAccountRelationRepository: UserThirdPartyAccountRelationRepository,
    private val thirdPartyAccountRelationService: ThirdPartyAccountRelationService,
    private val expiringKVStore: ExpiringKVStore<String, Any?> = InMemoryKVStore()
) : UserThirdPartyAccountBindService {
    private val logger = logger()

    override fun getRepository(): UserThirdPartyAccountRelationRepository {
        return this.userThirdPartyAccountRelationRepository
    }

    override fun generateUserBindCode(thirdPartyAccountEntityId: Long): String {
        val code = (10000..999999).random().toString()
        val key = "user_3rd_account_bind_code:$code"

        this.expiringKVStore.set(key, thirdPartyAccountEntityId, 5 * 60 * 1000L)

        return code
    }

    @Transactional
    override fun bind(userId: Long, code: String) {
        val key = "user_3rd_account_bind_code:$code"

        val bindTargetAccountId = this.expiringKVStore[key]?.toString()?.toLong()
            ?: throw BusinessException("bind code is invalid")

        this.thirdPartyAccountRelationService.bindUserToThirdPartyAccount(
            userId,
            bindTargetAccountId
        )

        logger.info("User $userId is bind to 3rd party account $bindTargetAccountId")

        this.expiringKVStore.remove(key)
    }

    @Transactional
    override fun unbind(userId: Long, thirdPartyAccountEntityId: Long) {
        this.thirdPartyAccountRelationService.unbindUserFromThirdPartyAccount(
            userId,
            thirdPartyAccountEntityId
        )

        logger.info("User $userId is unbind from 3rd party account $thirdPartyAccountEntityId")
    }
}