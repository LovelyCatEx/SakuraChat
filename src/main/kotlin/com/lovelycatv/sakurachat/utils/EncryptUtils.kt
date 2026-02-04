/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.utils

import java.math.BigInteger
import java.security.MessageDigest

object EncryptUtils {
    fun calculateHash(input: String, algorithm: String): String {
        val messageDigest = MessageDigest.getInstance(algorithm)
        val hashBytes = messageDigest.digest(input.toByteArray())
        return BigInteger(1, hashBytes).toString(16).padStart(
            when (algorithm) {
                "SHA-1" -> 40      // 160位 = 40个十六进制字符
                "SHA-256" -> 64    // 256位 = 64个十六进制字符
                else -> 64
            },
            '0'
        )
    }
}