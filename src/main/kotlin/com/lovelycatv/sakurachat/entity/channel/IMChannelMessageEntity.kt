/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.entity.channel

import com.lovelycatv.sakurachat.types.ChannelMemberType
import com.lovelycatv.sakurachat.types.ThirdPartyPlatform
import jakarta.persistence.*

@Entity
@Table(name = "im_channel_messages")
data class IMChannelMessageEntity(
    @Id
    @Column(name = "id", nullable = false)
    val id: Long = 0,
    @Column(name = "channel_id", nullable = false)
    var channelId: Long = 0,
    @Column(name = "member_type", nullable = false)
    val memberType: Int = ChannelMemberType.USER.typeCode,
    @Column(name = "member_id", nullable = false)
    val memberId: Long = 0,
    @Column(name = "platform", nullable = false)
    val platform: Int = ThirdPartyPlatform.NAPCAT_OICQ.platformId,
    @Column(name = "content", nullable = false)
    val content: String = "",
    @Column(name = "created_time", nullable = false)
    val createdTime: Long = 0,
    @Column(name = "modified_time", nullable = false)
    val modifiedTime: Long = 0,
    @Column(name = "deleted_time", nullable = true)
    val deletedTime: Long? = null
) {
    fun getRealMemberType(): ChannelMemberType {
        return ChannelMemberType.getByTypeCode(this.memberType)
            ?: throw IllegalArgumentException("Unknown member type: ${this.memberType}")
    }

    fun getRealPlatform(): ThirdPartyPlatform {
        return ThirdPartyPlatform.getByPlatformId(this.platform)
            ?: throw IllegalArgumentException("Unknown platform type: ${this.platform}")
    }
}