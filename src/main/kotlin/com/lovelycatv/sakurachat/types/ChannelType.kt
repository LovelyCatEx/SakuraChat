/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.types

enum class ChannelType(val channelTypeId: Int) {
    PRIVATE(0),
    GROUP(1);

    companion object {
        fun getByChannelTypeId(channelTypeId: Int): ChannelType? {
            return entries.find { it.channelTypeId == channelTypeId }
        }
    }
}