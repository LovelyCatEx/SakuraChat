/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.service

import com.lovelycatv.sakurachat.adapters.thirdparty.group.IThirdPartyGroupAdapter
import com.lovelycatv.sakurachat.entity.thirdparty.ThirdPartyGroupEntity
import com.lovelycatv.sakurachat.repository.ThirdPartyGroupRepository
import com.lovelycatv.sakurachat.types.ThirdPartyPlatform

interface ThirdPartyGroupService : BaseService<ThirdPartyGroupRepository, ThirdPartyGroupEntity, Long> {
    fun getGroupByPlatformAndGroupId(
        platform: ThirdPartyPlatform,
        groupId: String
    ): ThirdPartyGroupEntity?

    fun getGroupAdapters(platform: ThirdPartyPlatform, platformGroup: Class<out Any>): List<IThirdPartyGroupAdapter<Any>>

    fun getGroupAdapter(platform: ThirdPartyPlatform, platformGroup: Class<out Any>): IThirdPartyGroupAdapter<Any> {
        return this.getGroupAdapters(platform, platformGroup)
            .firstOrNull()
            ?: throw IllegalStateException("Third party group adapter not found for ${platformGroup::class.qualifiedName} of platform: ${platform.name}")
    }

    fun getOrAddGroup(platform: ThirdPartyPlatform, platformGroup: Any): ThirdPartyGroupEntity

    fun getGroupIdByPlatformGroupObject(platform: ThirdPartyPlatform, platformGroup: Any): String {
        return this.getGroupAdapter(platform, platformGroup::class.java).getGroupId(platformGroup)
    }
}