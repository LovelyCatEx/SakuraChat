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
import jakarta.persistence.Table
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction

@Entity
@Table(name = "credentials")
@SQLDelete(sql = "UPDATE credentials SET deleted_time = ROUND(UNIX_TIMESTAMP(CURTIME(3)) * 1000) WHERE id = ?")
@SQLRestriction(BaseEntity.SOFT_NON_DELETED_RESTRICTION)
class CredentialEntity(
    override val id: Long? = null,
    @Column(name = "type", nullable = false)
    var type: Int = CredentialType.AUTHORIZATION_BEARER.typeId,
    @Column(name = "data", nullable = false)
    var data: String = "",
    @Column(name = "active", nullable = false)
    var active: Boolean = true,
    override val createdTime: Long = System.currentTimeMillis(),
    override var modifiedTime: Long = System.currentTimeMillis(),
    override var deletedTime: Long? = null
) : BaseEntity() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CredentialEntity) return false

        if (id != other.id)
            return false
        if (type != other.type)
            return false
        if (data != other.data)
            return false
        if (active != other.active)
            return false
        if (createdTime != other.createdTime)
            return false
        if (modifiedTime != other.modifiedTime)
            return false
        return deletedTime == other.deletedTime
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + type.hashCode()
        result = 31 * result + data.hashCode()
        result = 31 * result + active.hashCode()
        result = 31 * result + createdTime.hashCode()
        result = 31 * result + modifiedTime.hashCode()
        result = 31 * result + (deletedTime?.hashCode() ?: 0)
        return result
    }
}
