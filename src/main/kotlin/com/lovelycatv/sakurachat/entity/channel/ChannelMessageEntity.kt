/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.entity.channel

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull

@Entity
@Table(name = "channel_messages")
data class ChannelMessageEntity(
    @Id
    @Column(name = "id", nullable = false)
    val id: Long = 0,
    @Column(name = "channel_id", nullable = false)
    var channelId: Long = 0,
    @Column(name = "member_id", nullable = false, length = 128)
    val memberId: String = "",
    @Column(name = "content", nullable = false)
    val content: String = "",
    @Column(name = "created_time", nullable = false)
    val createdTime: Long = 0,
    @Column(name = "modified_time", nullable = false)
    val modifiedTime: Long = 0,
    @Column(name = "deleted_time", nullable = true)
    val deletedTime: Long? = null
)