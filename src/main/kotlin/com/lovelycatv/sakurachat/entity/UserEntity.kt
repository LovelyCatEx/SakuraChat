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
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

@Entity
@Table(name = "users")
@SQLDelete(sql = "UPDATE users SET deleted_time = ROUND(UNIX_TIMESTAMP(CURTIME(3)) * 1000) WHERE id = ?")
@SQLRestriction(BaseEntity.SOFT_NON_DELETED_RESTRICTION)
class UserEntity(
    override val id: Long = 0,
    @Column(name = "username", length = 64, nullable = false, unique = true)
    private val username: String = "",
    @Column(name = "password", length = 128, nullable = false)
    private val password: String = "",
    @Column(name = "nickname", length = 64, nullable = false)
    var nickname: String = "",
    @Column(name = "email", length = 256, nullable = false, unique = true)
    var email: String = "",
    @Column(name = "points", nullable = false)
    var points: Long = 0,
    override val createdTime: Long = System.currentTimeMillis(),
    override var modifiedTime: Long = System.currentTimeMillis(),
    override var deletedTime: Long? = null
) : BaseEntity(), UserDetails {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return mutableListOf()
    }

    override fun getUsername(): String = this.username
    override fun getPassword(): String = this.password

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UserEntity) return false

        if (id != other.id)
            return false
        if (username != other.username)
            return false
        if (password != other.password)
            return false
        if (nickname != other.nickname)
            return false
        if (email != other.email)
            return false
        if (points != other.points)
            return false
        if (createdTime != other.createdTime)
            return false
        if (modifiedTime != other.modifiedTime)
            return false
        return deletedTime == other.deletedTime
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + username.hashCode()
        result = 31 * result + password.hashCode()
        result = 31 * result + nickname.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + points.hashCode()
        result = 31 * result + createdTime.hashCode()
        result = 31 * result + modifiedTime.hashCode()
        result = 31 * result + (deletedTime?.hashCode() ?: 0)
        return result
    }
}