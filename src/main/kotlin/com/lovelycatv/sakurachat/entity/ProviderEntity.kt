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
@Table(name = "providers")
@SQLDelete(sql = "UPDATE providers SET deleted_time = ROUND(UNIX_TIMESTAMP(CURTIME(3)) * 1000) WHERE id = ?")
@SQLRestriction(BaseEntity.SOFT_NON_DELETED_RESTRICTION)
class ProviderEntity(
    override val id: Long? = null,
    @Column(name = "name", length = 32, nullable = false, unique = true)
    var name: String = "",
    @Column(name = "description", length = 512, nullable = true)
    var description: String? = null,
    @Column(name = "icon", nullable = true)
    var icon: ByteArray? = null,
    @Column(name = "api_type", nullable = false)
    var apiType: Int = ApiType.OPEN_AI.typeId,
    @Column(name = "chat_completions_url", nullable = false)
    var chatCompletionsUrl: String = "",
    override val createdTime: Long = System.currentTimeMillis(),
    override var modifiedTime: Long = System.currentTimeMillis(),
    override var deletedTime: Long? = null
): BaseEntity() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ProviderEntity) return false

        if (id != other.id) return false
        if (name != other.name) return false
        if (description != other.description) return false
        if (icon != null) {
            if (other.icon == null) return false
            if (!icon.contentEquals(other.icon)) return false
        } else if (other.icon != null) return false
        if (createdTime != other.createdTime) return false
        if (modifiedTime != other.modifiedTime) return false
        return deletedTime == other.deletedTime
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + name.hashCode()
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + (icon?.contentHashCode() ?: 0)
        result = 31 * result + createdTime.hashCode()
        result = 31 * result + modifiedTime.hashCode()
        result = 31 * result + (deletedTime?.hashCode() ?: 0)
        return result
    }
}