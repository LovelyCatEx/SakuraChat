/*
 * Copyright 2025 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */
package com.lovelycatv.sakurachat.service

import com.lovelycatv.sakurachat.entity.UserEntity
import com.lovelycatv.sakurachat.types.ThirdPartyPlatform
import org.springframework.security.core.userdetails.UserDetailsService

interface UserService : UserDetailsService {
    suspend fun register(username: String, password: String, email: String, emailCode: String)

    suspend fun getUserByThirdPartyAccount(
        platform: ThirdPartyPlatform,
        accountId: String
    ): UserEntity?
}