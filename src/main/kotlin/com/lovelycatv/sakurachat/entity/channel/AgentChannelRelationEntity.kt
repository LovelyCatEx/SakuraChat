/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.entity.channel

import jakarta.persistence.*

@Entity
@Table(name = "agent_channel_relations")
data class AgentChannelRelationEntity(
    @EmbeddedId
    val primaryKey: PrimaryKey
) {
    @Embeddable
    data class PrimaryKey(
        @Column(name = "channel_id", nullable = false)
        val channelId: Long,
        @Column(name = "agent_id", nullable = false)
        val agentId: Long,
    )
}
