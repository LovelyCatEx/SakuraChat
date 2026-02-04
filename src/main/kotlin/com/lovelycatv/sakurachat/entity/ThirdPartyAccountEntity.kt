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
@Table(name = "third_party_accounts")
data class ThirdPartyAccountEntity(
    @Id
    @Column(name = "id", nullable = false)
    val id: Long,
    @Column(name = "account_id", nullable = false, length = 64)
    var accountId: String? = null,
    @Column(name = "nickname", nullable = false, length = 256)
    var nickname: String? = null,
    @Column(name = "platform", nullable = false)
    var platform: Int? = null,
    @Column(name = "created_time", nullable = false)
    var createdTime: Long? = null,
    @Column(name = "modified_time", nullable = false)
    var modifiedTime: Long? = null,
    @Column(name = "deleted_time")
    var deletedTime: Long? = null,
)