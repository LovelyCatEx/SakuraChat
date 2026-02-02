/*
 * Copyright 2025 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "chat_models")
data class ChatModelEntity(
    @Id
    @Column(name = "id", nullable = false)
    val id: Long? = null,
    @Column(name = "name", length = 64, nullable = false, unique = true)
    private val name: String = "",
    @Column(name = "description", length = 512, nullable = true)
    private val description: String? = null,
    @Column(name = "provider_id", nullable = false)
    val providerId: Long = 0,
    @Column(name = "qualified_name", length = 256, nullable = false)
    val qualifiedName: String = "",
    @Column(name = "max_context_tokens", nullable = false)
    val maxContextTokens: Int = -1,
    @Column(name = "temperature", nullable = false)
    val temperature: Int = -1,
    @Column(name = "max_tokens", nullable = false)
    val maxTokens: Int = -1,
    @Column(name = "token_point_rate", nullable = false)
    val tokenPointRate: Int = 10000,
    @Column(name = "credential_id", nullable = false)
    val credentialId: Long = 0,
    @Column(name = "active", nullable = false)
    val active: Boolean = true,
    @Column(name = "created_time", nullable = false)
    val createdTime: Long = System.currentTimeMillis(),
    @Column(name = "modified_time", nullable = false)
    val modifiedTime: Long = System.currentTimeMillis(),
    @Column(name = "deleted_time", nullable = true)
    val deletedTime: Long? = null
)
