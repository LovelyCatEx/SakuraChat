/*
 * Copyright 2025 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */
package com.lovelycatv.sakurachat.response

data class ApiResponse<T>(
    val code: Int,
    val message: String,
    val data: T?
) {
    companion object {
        @JvmStatic
        fun <T> success(data: T?, message: String = "success") = ApiResponse(200, message, data)

        @JvmStatic
        fun <T> unauthorized(message: String, data: T? = null) = ApiResponse(401, message, data)

        @JvmStatic
        fun <T> forbidden(message: String, data: T? = null) = ApiResponse(403, message, data)

        @JvmStatic
        fun <T> badRequest(message: String, data: T? = null) = ApiResponse(400, message, data)

        @JvmStatic
        fun <T> internalServerError(message: String, data: T? = null) = ApiResponse(500, message, data)
    }
}