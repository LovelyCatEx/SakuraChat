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
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

@Entity
@Table(name = "users")
data class UserEntity (
    @Id
    @Column(name = "id", nullable = false)
    val id: Long? = null,
    @Column(name = "username", length = 64, nullable = false, unique = true)
    private val username: String = "",
    @Column(name = "password", length = 128, nullable = false)
    private val password: String = "",
    @Column(name = "nickname", length = 64, nullable = false)
    val nickname: String = "",
    @Column(name = "email", length = 256, nullable = false, unique = true)
    val email: String = "",
    @Column(name = "points", nullable = false)
    val points: Long = 0,
    @Column(name = "created_time", nullable = false)
    val createdTime: Long = System.currentTimeMillis(),
    @Column(name = "modified_time", nullable = false)
    val modifiedTime: Long = System.currentTimeMillis(),
    @Column(name = "deleted_time", nullable = true)
    val deletedTime: Long? = null
) : UserDetails {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return mutableListOf()
    }

    override fun getUsername(): String = this.username
    override fun getPassword(): String = this.password
}