/*
 * Copyright 2025 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.entity

enum class CredentialType(val typeId: Int) {
    AUTHORIZATION_BEARER(0),
    AUTHORIZATION_BASIC(1);

    companion object {
        fun getById(typeId: Int): CredentialType? {
            return entries.find { it.typeId == typeId }
        }
    }
}