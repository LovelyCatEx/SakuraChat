/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.types

data class SakuraChatSystemSettings(
    val userRegistration: UserRegistration,
    val mail: Mail,
) {
    data class UserRegistration(
        val initialPoints: Long,
    )

    data class Mail(
        val smtp: SMTP,
    ) {
        data class SMTP(
            val host: String,
            val port: Int,
            val username: String,
            val password: String,
            val ssl: Boolean,
            val fromEmail: String,
        )
    }
}
