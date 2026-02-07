/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.adapters.thirdparty.group

import com.lovelycatv.sakurachat.types.ThirdPartyPlatform
import com.mikuac.shiro.dto.action.response.GroupInfoResp
import org.springframework.stereotype.Component

@Component
class NapCatGroupAdapter : IThirdPartyGroupAdapter<GroupInfoResp> {
    override fun getPlatform(): ThirdPartyPlatform {
        return ThirdPartyPlatform.NAPCAT_OICQ
    }

    override fun getThirdPartyGroupClass(): Class<GroupInfoResp> {
        return GroupInfoResp::class.java
    }

    override fun getGroupId(thirdPartyGroup: GroupInfoResp): String {
        return thirdPartyGroup.groupId.toString()
    }

    override fun getGroupName(thirdPartyGroup: GroupInfoResp): String {
        return thirdPartyGroup.groupName
    }
}