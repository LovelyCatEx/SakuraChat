/*
 * Copyright 2026 lovelycat
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
@Table(name = "lark_bot_applications")
data class LarkBotApplicationEntity(
    @Id
    @Column(name = "id", nullable = false)
    val id: Long = 0,
    @Column(name = "name", nullable = false, length = 64)
    var name: String = "",
    @Column(name = "owner_user_id", nullable = false)
    var ownerUserId: Long = 0,
    @Column(name = "app_id", nullable = false, length = 256)
    var appId: String = "",
    @Column(name = "app_secret", nullable = false, length = 256)
    var appSecret: String = "",
    @Column(name = "encrypt_key", nullable = false, length = 256)
    var encryptKey: String = "",
    @Column(name = "verification_token", nullable = false, length = 256)
    var verificationToken: String = "",
    @Column(name = "created_time", nullable = false)
    val createdTime: Long = 0,
    @Column(name = "modified_time", nullable = false)
    var modifiedTime: Long = 0,
    @Column(name = "deleted_time", nullable = true)
    var deletedTime: Long? = null
)