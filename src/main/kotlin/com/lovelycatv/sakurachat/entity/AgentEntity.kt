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
import jakarta.validation.constraints.Size

@Entity
@Table(name = "agents")
data class AgentEntity(
    @Id
    @Column(name = "id", nullable = false)
    val id: Long? = null,
    @Column(name = "name", length = 32, nullable = false, unique = true)
    val name: String = "",
    @Column(name = "description", length = 512, nullable = true)
    val description: String? = null,
    @Column(name = "prompt", nullable = false)
    val prompt: String = "",
    @Column(name = "delimiter", length = 16)
    var delimiter: String? = null,
    @Column(name = "user_id", nullable = false)
    val userId: Long = 0,
    @Column(name = "chat_model_id", nullable = false)
    val chatModelId: Long = 0,
    @Column(name = "created_time", nullable = false)
    val createdTime: Long = System.currentTimeMillis(),
    @Column(name = "modified_time", nullable = false)
    val modifiedTime: Long = System.currentTimeMillis(),
    @Column(name = "deleted_time", nullable = true)
    val deletedTime: Long? = null
)
