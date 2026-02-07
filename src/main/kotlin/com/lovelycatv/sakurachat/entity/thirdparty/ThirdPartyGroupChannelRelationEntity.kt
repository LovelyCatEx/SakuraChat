/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.entity.thirdparty

import jakarta.persistence.*

@Entity
@Table(name = "third_party_group_channel_relations")
data class ThirdPartyGroupChannelRelationEntity(
    @EmbeddedId
    val primaryKey: PrimaryKey
) {
    @Embeddable
    data class PrimaryKey(
        @Column(name = "third_party_group_id")
        val thirdPartyGroupId: Long,
        @Column(name = "channel_id")
        val channelId: Long
    )
}
