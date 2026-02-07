/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.types

enum class ThirdPartyPlatform(val platformId: Int) {
    OICQ(0),
    LARK(1);

    companion object {
        fun getByPlatformId(platformId: Int): ThirdPartyPlatform? {
            return entries.find { it.platformId == platformId }
        }
    }
}