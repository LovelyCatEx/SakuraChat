/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.entity.channel

import com.lovelycatv.sakurachat.types.ChannelType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.annotations.ColumnDefault

@Entity
@Table(name = "im_channels")
data class IMChannelEntity(
    @Id
    @Column(name = "id", nullable = false)
    val id: Long = 0,
    @Column(name = "channel_name", nullable = false, length = 64)
    var channelName: String = "",
    @Column(name = "channel_identifier", nullable = false, length = 256)
    var channelIdentifier: String = "",
    @ColumnDefault("0")
    @Column(name = "channel_type", nullable = false)
    var channelType: Int = ChannelType.PRIVATE.channelTypeId,
    @Column(name = "created_time", nullable = false)
    val createdTime: Long = System.currentTimeMillis(),
    @Column(name = "modified_time", nullable = false)
    val modifiedTime: Long = System.currentTimeMillis(),
    @Column(name = "deleted_time", nullable = true)
    val deletedTime: Long? = null
) {
    fun getRealChannelType(): ChannelType {
        return ChannelType.getByChannelTypeId(this.channelType)!!
    }
}