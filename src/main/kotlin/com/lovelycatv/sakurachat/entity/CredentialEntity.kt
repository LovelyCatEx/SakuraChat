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
@Table(name = "credentials")
data class CredentialEntity(
    @Id
    @Column(name = "id", nullable = false)
    val id: Long? = null,
    @Column(name = "type", nullable = false)
    val type: Int = CredentialType.AUTHORIZATION_BEARER.typeId,
    @Column(name = "data", nullable = false)
    val data: String = "",
    @Column(name = "active", nullable = false)
    val active: Boolean = true,
    @Column(name = "created_time", nullable = false)
    val createdTime: Long = System.currentTimeMillis(),
    @Column(name = "modified_time", nullable = false)
    val modifiedTime: Long = System.currentTimeMillis(),
    @Column(name = "deleted_time", nullable = true)
    val deletedTime: Long? = null
)
