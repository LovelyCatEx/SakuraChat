/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.service.impl

import com.lovelycatv.sakurachat.adapters.ThirdPartyAccountAdapter
import com.lovelycatv.sakurachat.entity.ThirdPartyAccountEntity
import com.lovelycatv.sakurachat.exception.BusinessException
import com.lovelycatv.sakurachat.repository.ThirdPartyAccountRepository
import com.lovelycatv.sakurachat.service.ThirdPartyAccountService
import com.lovelycatv.sakurachat.types.ThirdPartyPlatform
import com.lovelycatv.sakurachat.utils.SnowIdGenerator
import com.lovelycatv.vertex.log.logger
import org.springframework.beans.factory.getBeansOfType
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Service

@Service
class ThirdPartyAccountServiceImpl(
    private val applicationContext: ApplicationContext,
    private val snowIdGenerator: SnowIdGenerator,
    private val thirdPartyAccountRepository: ThirdPartyAccountRepository
) : ThirdPartyAccountService {
    private val logger = logger()

    override fun getRepository(): ThirdPartyAccountRepository {
        return this.thirdPartyAccountRepository
    }

    override fun getAccountByPlatformAndAccountId(
        platform: ThirdPartyPlatform,
        accountId: String
    ): ThirdPartyAccountEntity? {
        return thirdPartyAccountRepository.getByAccountIdAndPlatform(accountId, platform.platformId)
    }

    override fun getOrAddAccount(
        platform: ThirdPartyPlatform,
        platformAccount: Any
    ): ThirdPartyAccountEntity {
        val adapters = this.getThirdPartyAccountAdapter(platform, platformAccount::class.java)
        if (adapters.isEmpty()) {
            throw BusinessException("No third party account adapter found for platform $platform")
        }

        val adapter = adapters.first()

        val accountEntity = adapter
            .safeTransform(platformAccount)
            .apply {
                this.id = snowIdGenerator.nextId()
                this.createdTime = System.currentTimeMillis()
                this.modifiedTime = this.createdTime
                this.deletedTime = null
            }

        val existingAccount = this.getAccountByPlatformAndAccountId(platform, accountEntity.accountId)

        if (existingAccount != null) {
            if (existingAccount.nickname != accountEntity.nickname) {
                this.thirdPartyAccountRepository.save(
                    existingAccount.apply {
                        this.nickname = accountEntity.nickname
                    }
                ).also {
                    logger.info("Third party account ${accountEntity.accountId} of platform ${platform.name} was updated")
                }
            }
        }

        return existingAccount ?: this.thirdPartyAccountRepository.save(accountEntity).also {
            logger.info("Third party account added for platform $platform, data: $accountEntity, original: $platformAccount")
        }
    }

    fun getThirdPartyAccountAdapter(
        platform: ThirdPartyPlatform,
        thirdPartyAccountClass: Class<out Any>
    ): List<ThirdPartyAccountAdapter<Any>> {
        return applicationContext
            .getBeansOfType<ThirdPartyAccountAdapter<Any>>()
            .values
            .filter {
                it.getPlatform() == platform
                        && it.getThirdPartyAccountClass().simpleName == thirdPartyAccountClass.simpleName
                        && it.getThirdPartyAccountClass().canonicalName == thirdPartyAccountClass.canonicalName
            }
    }
}