/*
 * Copyright 2025 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.entity

enum class ApiType(val typeId: Int) {
    OPEN_AI(0),
    CLAUDE(1),
    GEMINI(2);

    companion object {
        fun getById(typeId: Int): ApiType? {
            return entries.find { it.typeId == typeId }
        }
    }
}