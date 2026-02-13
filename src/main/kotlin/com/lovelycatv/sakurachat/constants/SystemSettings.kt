/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.constants

object SystemSettings {
    object System {
        const val SYSTEM_INITIALIZED = "system.initialized"
    }
    object UserRegistration {
        const val INITIAL_POINTS = "user_registration.initial_points"
    }

    object Mail {
        object SMTP {
            const val HOST = "mail.smtp.host"
            const val PORT = "mail.smtp.port"
            const val USERNAME = "mail.smtp.username"
            const val PASSWORD = "mail.smtp.password"
            const val SSL = "mail.smtp.ssl"
            const val FROM_EMAIL = "mail.smtp.fromEmail"
        }
    }
}