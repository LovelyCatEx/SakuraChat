/*
 * Copyright 2025 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.entity.napcat

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "napcat_private_messages")
data class NapCatPrivateMessageEntity(
    @Id
    @Column(name = "id", nullable = false)
    val id: Long? = null,
    @Column(name = "message_id", nullable = false)
    val messageId: Int = 0,
    @Column(name = "bot_id", nullable = false)
    val botId: Long = 0,
    @Column(name = "sender_id", nullable = false)
    val senderId: Long = 0,
    @Column(name = "sender_nickname", length = 64, nullable = false)
    val senderNickname: String = "",
    @Column(name = "message", nullable = false)
    val message: String = "",
    @Column(name = "created_time", nullable = false)
    val createdTime: Long = 0,
)