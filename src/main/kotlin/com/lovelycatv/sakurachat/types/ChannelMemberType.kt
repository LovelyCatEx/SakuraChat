/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.types

enum class ChannelMemberType(val typeCode: Int) {
    USER(0),
    AGENT(1);

    companion object {
        fun getByTypeCode(typeCode: Int): ChannelMemberType? {
            return entries.find { it.typeCode == typeCode }
        }
    }
}