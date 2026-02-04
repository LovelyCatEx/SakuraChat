/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.entity

import jakarta.persistence.*

@Entity
@Table(name = "user_third_party_account_relations")
data class UserThirdPartyAccountRelationEntity(
    @EmbeddedId
    val primaryKey: PrimaryKey
) {
    @Embeddable
    data class PrimaryKey(
        @Column(name = "user_id", nullable = false)
        val userId: Long,
        @Column(name = "third_party_account_id", nullable = false)
        val thirdPartyAccountId: Long
    )
}