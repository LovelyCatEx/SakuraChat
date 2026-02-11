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
@Table(name = "user_roles")
data class UserRole(
    @Id
    @Column(name = "id", nullable = false)
    val id: Long? = null,
    @Column(name = "name", length = 32, nullable = false, unique = true)
    var name: String = "",
    @Column(name = "description", length = 256, nullable = true, unique = true)
    var description: String? = null,
    @Column(name = "created_time", nullable = false)
    val createdTime: Long = System.currentTimeMillis(),
    @Column(name = "modified_time", nullable = false)
    var modifiedTime: Long = System.currentTimeMillis(),
    @Column(name = "deleted_time", nullable = true)
    var deletedTime: Long? = null
)