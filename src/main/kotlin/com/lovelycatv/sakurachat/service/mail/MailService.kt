/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.service.mail

interface MailService {
    fun refreshSettings()

    suspend fun sendMail(to: String, subject: String, content: String)

    suspend fun sendRegisterEmail(to: String, emailCode: String)
}