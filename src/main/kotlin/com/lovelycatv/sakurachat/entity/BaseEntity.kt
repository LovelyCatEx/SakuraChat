/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.entity

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import jakarta.persistence.Column
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.PreUpdate

@MappedSuperclass
abstract class BaseEntity {
    @Id
    @get:JsonSerialize(using = ToStringSerializer::class)
    val id: Long = 0
    @Column(name = "created_time", nullable = false, updatable = false)
    val createdTime: Long = System.currentTimeMillis()
    @Column(name = "modified_time", nullable = false)
    var modifiedTime: Long = System.currentTimeMillis()
    @Column(name = "deleted_time")
    var deletedTime: Long? = null

    companion object {
        const val SOFT_NON_DELETED_RESTRICTION = "deleted_time IS NULL"
        const val SOFT_DELETED_RESTRICTION = "deleted_time IS NOT NULL"
    }

    @PreUpdate
    fun preUpdate() {
        modifiedTime = System.currentTimeMillis()
    }
}