/*
 * Copyright 2025 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.entity

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Table
import jakarta.validation.constraints.Size
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction

@Entity
@Table(name = "agents")
@SQLDelete(sql = "UPDATE agents SET deleted_time = ROUND(UNIX_TIMESTAMP(CURTIME(3)) * 1000) WHERE id = ?")
@SQLRestriction(BaseEntity.SOFT_NON_DELETED_RESTRICTION)
class AgentEntity(
    override val id: Long = 0,
    @Column(name = "name", length = 32, nullable = false, unique = true)
    var name: String = "",
    @Column(name = "description", length = 512, nullable = true)
    var description: String? = null,
    @Column(name = "prompt", nullable = false)
    var prompt: String = "",
    @Column(name = "delimiter", length = 16)
    var delimiter: String? = null,
    @Column(name = "user_id", nullable = false)
    var userId: Long = 0,
    @Column(name = "chat_model_id", nullable = false)
    @get:JsonSerialize(using = ToStringSerializer::class)
    var chatModelId: Long = 0,
    override val createdTime: Long = System.currentTimeMillis(),
    override var modifiedTime: Long = System.currentTimeMillis(),
    override var deletedTime: Long? = null
) : BaseEntity() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AgentEntity) return false

        if (id != other.id)
            return false
        if (name != other.name)
            return false
        if (description != other.description)
            return false
        if (prompt != other.prompt)
            return false
        if (delimiter != other.delimiter)
            return false
        if (userId != other.userId)
            return false
        if (chatModelId != other.chatModelId)
            return false
        if (createdTime != other.createdTime)
            return false
        if (modifiedTime != other.modifiedTime)
            return false
        return deletedTime == other.deletedTime
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + name.hashCode()
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + prompt.hashCode()
        result = 31 * result + (delimiter?.hashCode() ?: 0)
        result = 31 * result + userId.hashCode()
        result = 31 * result + chatModelId.hashCode()
        result = 31 * result + createdTime.hashCode()
        result = 31 * result + modifiedTime.hashCode()
        result = 31 * result + (deletedTime?.hashCode() ?: 0)
        return result
    }
}
