/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.core

import com.lovelycatv.sakurachat.adapters.thirdparty.im.ThirdPartyIMAccessorManager
import com.lovelycatv.sakurachat.entity.UserEntity
import org.springframework.stereotype.Component

@Component
class SakuraChatUserInstanceManager(
    private val thirdPartyIMAccessorManager: ThirdPartyIMAccessorManager
) {
    private val instances = mutableMapOf<String, SakuraChatUser>()

    fun getUser(userId: Long): SakuraChatUser? {
        return instances[SakuraChatUser.buildMemberId(userId)]
    }

    fun addUser(user: SakuraChatUser): SakuraChatUser {
        instances[user.memberId] = user
        return user
    }

    fun addUser(user: UserEntity): SakuraChatUser {
        return this.addUser(
            SakuraChatUser(
                user = user,
                thirdPartyIMAccessorManager = thirdPartyIMAccessorManager
            )
        )
    }
}