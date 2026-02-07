/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.service.impl

import com.lovelycatv.sakurachat.adapters.thirdparty.group.IThirdPartyGroupAdapter
import com.lovelycatv.sakurachat.adapters.thirdparty.group.ThirdPartyGroupAdapterManager
import com.lovelycatv.sakurachat.entity.thirdparty.ThirdPartyGroupEntity
import com.lovelycatv.sakurachat.repository.ThirdPartyGroupRepository
import com.lovelycatv.sakurachat.service.ThirdPartyGroupService
import com.lovelycatv.sakurachat.types.ThirdPartyPlatform
import com.lovelycatv.sakurachat.utils.SnowIdGenerator
import com.lovelycatv.vertex.log.logger
import org.springframework.stereotype.Service

@Service
class ThirdPartyGroupServiceImpl(
    private val thirdPartyGroupRepository: ThirdPartyGroupRepository,
    private val thirdPartyGroupAdapterManager: ThirdPartyGroupAdapterManager,
    private val snowIdGenerator: SnowIdGenerator
) : ThirdPartyGroupService {
    private val logger = logger()

    override fun getRepository(): ThirdPartyGroupRepository {
        return this.thirdPartyGroupRepository
    }

    override fun getGroupByPlatformAndGroupId(
        platform: ThirdPartyPlatform,
        groupId: String
    ): ThirdPartyGroupEntity? {
        return this.getRepository().findByPlatformAndGroupId(platform.platformId, groupId)
    }

    override fun getGroupAdapters(
        platform: ThirdPartyPlatform,
        platformGroup: Class<out Any>
    ): List<IThirdPartyGroupAdapter<Any>> {
        return this.thirdPartyGroupAdapterManager.getAdapters(platform, platformGroup)
    }

    override fun getOrAddGroup(
        platform: ThirdPartyPlatform,
        platformGroup: Any
    ): ThirdPartyGroupEntity {
        val adapter = super.getGroupAdapter(platform, platformGroup::class.java)

        val groupEntity = ThirdPartyGroupEntity(
            id = snowIdGenerator.nextId(),
            platform = platform.platformId,
            name = adapter.getGroupName(platformGroup),
            groupId = adapter.getGroupId(platformGroup),
            createdTime = System.currentTimeMillis(),
            modifiedTime = System.currentTimeMillis(),
            deletedTime = null
        )


        val existingGroup = this.getGroupByPlatformAndGroupId(platform, groupEntity.groupId)

        if (existingGroup != null) {
            if (existingGroup.name != groupEntity.name) {
                this.getRepository().save(
                    existingGroup.apply {
                        this.name = groupEntity.name
                    }
                ).also {
                    logger.info("Third party group ${groupEntity.groupId} of platform ${platform.name} was updated")
                }
            }
        }

        return existingGroup ?: this.getRepository().save(groupEntity).also {
            logger.info("Third party group added for platform $platform, data: $groupEntity, original: $platformGroup")
        }
    }
}