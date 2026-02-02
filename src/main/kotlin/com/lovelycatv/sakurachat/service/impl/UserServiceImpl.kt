/*
 * Copyright 2025 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */
package com.lovelycatv.sakurachat.service.impl

import com.lovelycatv.sakurachat.entity.UserEntity
import com.lovelycatv.sakurachat.exception.BusinessException
import com.lovelycatv.sakurachat.repository.UserRepository
import com.lovelycatv.sakurachat.service.UserService
import com.lovelycatv.sakurachat.utils.SnowIdGenerator
import jakarta.annotation.Resource
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserServiceImpl : UserService {
    @Resource
    private lateinit var userRepository: UserRepository
    @Resource
    private lateinit var passwordEncoder: PasswordEncoder
    @Resource
    private lateinit var snowIdGenerator: SnowIdGenerator

    override suspend fun register(username: String, password: String, email: String, emailCode: String) {
        val userByName = userRepository.findByUsername(username)
        if (userByName != null) {
            throw BusinessException("user $username already registered")
        }

        val userByEmail = userRepository.findByEmail(email)
        if (userByEmail != null) {
            throw BusinessException("user $email already registered")
        }

        userRepository.save(
            UserEntity(
                snowIdGenerator.nextId(),
                username,
                passwordEncoder.encode(password),
                username,
                email
            )
        )
    }

    override fun loadUserByUsername(username: String?): UserDetails {
        if (username == null) {
            throw BusinessException("Username could not be null")
        }

        val user = userRepository.findByUsername(username)
            ?: userRepository.findByEmail(username)
            ?: throw BusinessException("User $username not found")

        return user
    }
}