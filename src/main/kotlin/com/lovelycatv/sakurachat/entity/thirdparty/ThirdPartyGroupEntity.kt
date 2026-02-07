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
@Table(name = "third_party_groups")
data class ThirdPartyGroupEntity(
    @Id
    @Column(name = "id", nullable = false)
    var id: Long = 0,
    @Column(name = "platform", nullable = false)
    var platform: Int = 0,
    @Column(name = "group_id", nullable = false, length = 128)
    var groupId: String = "",
    @Column(name = "name", nullable = false, length = 128)
    var name: String = "",
    @Column(name = "created_time", nullable = false)
    var createdTime: Long = 0,
    @Column(name = "modified_time", nullable = false)
    var modifiedTime: Long = 0,
    @Column(name = "deleted_time", nullable = true)
    var deletedTime: Long? = null
) {
    fun getPlatformType() = ThirdPartyPlatform.getByPlatformId(this.platform)
        ?: throw IllegalArgumentException("Third Party Platform ${this.platform} Not Found")
}
