/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.entity.thirdparty

import com.fasterxml.jackson.annotation.JsonIgnore
import com.lovelycatv.sakurachat.entity.BaseEntity
import com.lovelycatv.sakurachat.types.ThirdPartyPlatform
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction

@Entity
@Table(name = "third_party_accounts")
@SQLDelete(sql = "UPDATE third_party_accounts SET deleted_time = ROUND(UNIX_TIMESTAMP(CURTIME(3)) * 1000) WHERE id = ?")
@SQLRestriction(BaseEntity.SOFT_NON_DELETED_RESTRICTION)
class ThirdPartyAccountEntity(
    override val id: Long = 0,
    @Column(name = "account_id", nullable = false, length = 64)
    var accountId: String = "",
    @Column(name = "nickname", nullable = false, length = 256)
    var nickname: String = "",
    @Column(name = "platform", nullable = false)
    var platform: Int = 0,
    override val createdTime: Long = System.currentTimeMillis(),
    override var modifiedTime: Long = System.currentTimeMillis(),
    override var deletedTime: Long? = null
) : BaseEntity() {
    @JsonIgnore
    fun getPlatformType() = ThirdPartyPlatform.getByPlatformId(this.platform)
        ?: throw IllegalArgumentException("Third Party Platform ${this.platform} Not Found")

}