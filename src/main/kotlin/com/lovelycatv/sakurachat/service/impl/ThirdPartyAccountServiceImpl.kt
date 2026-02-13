/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.service.impl

import com.lovelycatv.sakurachat.adapters.thirdparty.account.ThirdPartyAccountAdapter
import com.lovelycatv.sakurachat.controller.manager.dto.ManagerCreateThirdPartyAccountDTO
import com.lovelycatv.sakurachat.entity.thirdparty.ThirdPartyAccountEntity
import com.lovelycatv.sakurachat.exception.BusinessException
import com.lovelycatv.sakurachat.repository.AgentThirdPartyAccountRelationRepository
import com.lovelycatv.sakurachat.repository.ThirdPartyAccountRepository
import com.lovelycatv.sakurachat.repository.UserThirdPartyAccountRelationRepository
import com.lovelycatv.sakurachat.request.PaginatedResponseData
import com.lovelycatv.sakurachat.service.ThirdPartyAccountService
import com.lovelycatv.sakurachat.types.ThirdPartyPlatform
import com.lovelycatv.sakurachat.utils.SnowIdGenerator
import com.lovelycatv.sakurachat.utils.toPaginatedResponseData
import com.lovelycatv.vertex.log.logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.getBeansOfType
import org.springframework.context.ApplicationContext
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class ThirdPartyAccountServiceImpl(
    private val applicationContext: ApplicationContext,
    private val snowIdGenerator: SnowIdGenerator,
    private val thirdPartyAccountRepository: ThirdPartyAccountRepository,
    private val agentThirdPartyAccountRelationRepository: AgentThirdPartyAccountRelationRepository,
    private val userThirdPartyAccountRelationRepository: UserThirdPartyAccountRelationRepository
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

    override fun getAccountAdapter(
        platform: ThirdPartyPlatform,
        platformAccount: Class<out Any>
    ): ThirdPartyAccountAdapter<Any> {
        val adapters = this.getThirdPartyAccountAdapter(platform, platformAccount)
        if (adapters.isEmpty()) {
            throw BusinessException("No third party account adapter found for platform $platform account ${platformAccount.canonicalName}")
        }

        return adapters.first()
    }

    override fun getOrAddAccount(
        platform: ThirdPartyPlatform,
        platformAccount: Any
    ): ThirdPartyAccountEntity {
        val adapter = this.getAccountAdapter(platform, platformAccount::class.java)

        val accountEntity = ThirdPartyAccountEntity(
            id = snowIdGenerator.nextId(),
            accountId = adapter.getAccountId(platformAccount),
            nickname = adapter.getNickName(platformAccount),
            platform = adapter.getPlatform().platformId,
            createdTime = System.currentTimeMillis(),
            modifiedTime = System.currentTimeMillis(),
            deletedTime = null
        )

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
                it.getPlatform() == platform && it.getThirdPartyAccountClass().isAssignableFrom(thirdPartyAccountClass)
            }
    }

    override suspend fun updateThirdPartyAccount(managerUpdateThirdPartyAccountDTO: com.lovelycatv.sakurachat.controller.manager.dto.ManagerUpdateThirdPartyAccountDTO) {
        val existing = this.getByIdOrThrow(managerUpdateThirdPartyAccountDTO.id)

        if (managerUpdateThirdPartyAccountDTO.accountId != null) {
            existing.accountId = managerUpdateThirdPartyAccountDTO.accountId
        }

        if (managerUpdateThirdPartyAccountDTO.nickname != null) {
            existing.nickname = managerUpdateThirdPartyAccountDTO.nickname
        }

        if (managerUpdateThirdPartyAccountDTO.platform != null) {
            existing.platform = managerUpdateThirdPartyAccountDTO.platform
        }

        withContext(Dispatchers.IO) {
            getRepository().save(existing)
        }
    }

    override suspend fun createThirdPartyAccount(managerCreateThirdPartyAccountDTO: ManagerCreateThirdPartyAccountDTO) {
        withContext(Dispatchers.IO) {
            getRepository().save(
                ThirdPartyAccountEntity(
                    id = snowIdGenerator.nextId(),
                    accountId = managerCreateThirdPartyAccountDTO.accountId,
                    nickname = managerCreateThirdPartyAccountDTO.nickname,
                    platform = managerCreateThirdPartyAccountDTO.platform
                )
            )
        }
    }

    override fun search(keyword: String, page: Int, pageSize: Int): PaginatedResponseData<ThirdPartyAccountEntity> {
        if (keyword.isBlank()) {
            return this.listByPage(page, pageSize).toPaginatedResponseData()
        }

        return getRepository().findAllByNicknameContainingIgnoreCaseOrAccountIdContainingIgnoreCase(
            keyword,
            keyword,
            Pageable.ofSize(pageSize).withPage(page - 1)
        ).toPaginatedResponseData()
    }

    override fun getUnboundAccountsForAgent(agentId: Long, page: Int, pageSize: Int): PaginatedResponseData<ThirdPartyAccountEntity> {
        // Get all bound account IDs from all agents
        val agentBoundAccountIds = agentThirdPartyAccountRelationRepository
            .findAll()
            .map { it.primaryKey.thirdPartyAccountId }

        // Get all bound account IDs from all users
        val userBoundAccountIds = userThirdPartyAccountRelationRepository
            .findAll()
            .map { it.primaryKey.thirdPartyAccountId }

        // Combine all bound account IDs
        val allBoundAccountIds = agentBoundAccountIds + userBoundAccountIds

        // Get all accounts not in the bound list with pagination
        val pageable = Pageable.ofSize(pageSize).withPage(page - 1)
        val allAccounts = thirdPartyAccountRepository.findAll(pageable)
        
        // Filter unbound accounts
        val unboundAccounts = allAccounts.filter { !allBoundAccountIds.contains(it.id) }

        // Get total count of unbound accounts
        val totalUnboundAccounts = thirdPartyAccountRepository.findAll()
            .filter { !allBoundAccountIds.contains(it.id) }
            .size

        // Create paginated response
        return PaginatedResponseData(
            page = page,
            pageSize = pageSize,
            total = totalUnboundAccounts.toLong(),
            totalPages = (totalUnboundAccounts + pageSize - 1) / pageSize,
            records = unboundAccounts.toList()
        )
    }
}