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
    var name: String = "",
    @Column(name = "description", length = 512, nullable = true)
    var description: String? = null,
    @Column(name = "provider_id", nullable = false)
    var providerId: Long = 0,
    @Column(name = "qualified_name", length = 256, nullable = false)
    var qualifiedName: String = "",
    @Column(name = "max_context_tokens", nullable = false)
    var maxContextTokens: Int = -1,
    @Column(name = "temperature", nullable = false)
    var temperature: Int = -1,
    @Column(name = "max_tokens", nullable = false)
    var maxTokens: Int = -1,
    @Column(name = "input_token_point_rate", nullable = false)
    var inputTokenPointRate: Int = 10000,
    @Column(name = "output_token_point_rate", nullable = false)
    var outputTokenPointRate: Int = 10000,
    @Column(name = "cached_input_token_point_rate", nullable = false)
    var cachedInputTokenPointRate: Int = 10000,
    @Column(name = "credential_id", nullable = false)
    var credentialId: Long = 0,
    @Column(name = "active", nullable = false)
    var active: Boolean = true,
    @Column(name = "created_time", nullable = false)
    val createdTime: Long = System.currentTimeMillis(),
    @Column(name = "modified_time", nullable = false)
    var modifiedTime: Long = System.currentTimeMillis(),
    @Column(name = "deleted_time", nullable = true)
    var deletedTime: Long? = null
) {
    fun getQualifiedTemperature(): Float? = this.temperature.run {
        if (this > 0) this / 100f else null
    }

    fun getQualifiedTokenPointRate(original: Int): Float = original.run {
        this / 10000f
    }
}