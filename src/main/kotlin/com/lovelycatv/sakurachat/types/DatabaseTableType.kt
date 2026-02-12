/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.types

enum class DatabaseTableType(val typeId: Int) {
    USERS(0),
    AGENTS(1),
    CHAT_MODELS(2),
    ;

    companion object {
        fun getById(id: Int): DatabaseTableType? {
            return entries.find { it.typeId == id }
        }
    }
}