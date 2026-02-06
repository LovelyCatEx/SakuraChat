/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.entity.thirdparty

import com.lovelycatv.sakurachat.types.ThirdPartyPlatform
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "third_party_accounts")
data class ThirdPartyAccountEntity(
    @Id
    @Column(name = "id", nullable = false)
    var id: Long = 0,
    @Column(name = "account_id", nullable = false, length = 64)
    var accountId: String = "",
    @Column(name = "nickname", nullable = false, length = 256)
    var nickname: String = "",
    @Column(name = "platform", nullable = false)
    var platform: Int = 0,
    @Column(name = "created_time", nullable = false)
    var createdTime: Long = System.currentTimeMillis(),
    @Column(name = "modified_time", nullable = false)
    var modifiedTime: Long = System.currentTimeMillis(),
    @Column(name = "deleted_time")
    var deletedTime: Long? = null,
) {
    fun getPlatformType() = ThirdPartyPlatform.getByPlatformId(this.platform)
        ?: throw IllegalArgumentException("Third Party Platform ${this.platform} Not Found")
}