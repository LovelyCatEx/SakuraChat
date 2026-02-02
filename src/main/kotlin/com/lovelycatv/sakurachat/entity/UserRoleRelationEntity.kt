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
@Table(name = "user_role_relations")
data class UserRoleRelationEntity(
    @Id
    @Column(name = "id", nullable = false)
    val id: Long? = null,
    @Column(name = "user_id", nullable = false)
    val userId: Long = 0,
    @Column(name = "role_id", nullable = false)
    val roleId: Long = 0
)
