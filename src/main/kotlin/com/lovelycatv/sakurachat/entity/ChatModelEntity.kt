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
import jakarta.persistence.Table
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction

@Entity
@Table(name = "chat_models")
@SQLDelete(sql = "UPDATE chat_models SET deleted_time = ROUND(UNIX_TIMESTAMP(CURTIME(3)) * 1000) WHERE id = ?")
@SQLRestriction(BaseEntity.SOFT_NON_DELETED_RESTRICTION)
class ChatModelEntity(
    override var id: Long = 0,
    @Column(name = "name", length = 64, nullable = false, unique = true)
    var name: String = "",
    @Column(name = "description", length = 512, nullable = true)
    var description: String? = null,
    @Column(name = "provider_id", nullable = false)
    @get:JsonSerialize(using = ToStringSerializer::class)
    var providerId: Long = 0,
    @Column(name = "credential_id", nullable = false)
    @get:JsonSerialize(using = ToStringSerializer::class)
    var credentialId: Long = 0,
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
    @Column(name = "active", nullable = false)
    var active: Boolean = true,
    override val createdTime: Long = System.currentTimeMillis(),
    override var modifiedTime: Long = System.currentTimeMillis(),
    override var deletedTime: Long? = null
) : BaseEntity() {
    fun getQualifiedTemperature(): Float? = this.temperature.run {
        if (this > 0) this / 100f else null
    }

    fun getQualifiedTokenPointRate(original: Int): Float = original.run {
        this / 10000f
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ChatModelEntity) return false

        if (id != other.id)
            return false
        if (name != other.name)
            return false
        if (description != other.description)
            return false
        if (providerId != other.providerId)
            return false
        if (qualifiedName != other.qualifiedName)
            return false
        if (maxContextTokens != other.maxContextTokens)
            return false
        if (temperature != other.temperature)
            return false
        if (maxTokens != other.maxTokens)
            return false
        if (inputTokenPointRate != other.inputTokenPointRate)
            return false
        if (outputTokenPointRate != other.outputTokenPointRate)
            return false
        if (cachedInputTokenPointRate != other.cachedInputTokenPointRate)
            return false
        if (credentialId != other.credentialId)
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
        result = 31 * result + name.hashCode()
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + providerId.hashCode()
        result = 31 * result + qualifiedName.hashCode()
        result = 31 * result + maxContextTokens.hashCode()
        result = 31 * result + temperature.hashCode()
        result = 31 * result + maxTokens.hashCode()
        result = 31 * result + inputTokenPointRate.hashCode()
        result = 31 * result + outputTokenPointRate.hashCode()
        result = 31 * result + cachedInputTokenPointRate.hashCode()
        result = 31 * result + credentialId.hashCode()
        result = 31 * result + active.hashCode()
        result = 31 * result + createdTime.hashCode()
        result = 31 * result + modifiedTime.hashCode()
        result = 31 * result + (deletedTime?.hashCode() ?: 0)
        return result
    }
}