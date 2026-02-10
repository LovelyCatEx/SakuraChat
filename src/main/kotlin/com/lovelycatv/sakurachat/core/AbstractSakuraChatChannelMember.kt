/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.core

import com.lovelycatv.sakurachat.types.ChannelMemberType

abstract class AbstractSakuraChatChannelMember(
    val memberType: ChannelMemberType
) : ISakuraChatMessageChannelMember {
    companion object {
        fun buildMemberId(memberType: ChannelMemberType, id: Long): String {
            return "${memberType.name}_$id"
        }
    }
    override val memberId: String get() = buildMemberId(this.memberType, this.id)

    abstract val id: Long
}