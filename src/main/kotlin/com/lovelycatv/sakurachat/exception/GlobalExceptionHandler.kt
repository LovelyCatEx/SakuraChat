/*
 * Copyright 2025 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */
package com.lovelycatv.sakurachat.exception

import com.lovelycatv.sakurachat.request.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(e: BusinessException): ResponseEntity<ApiResponse<*>?> {
        val response: ApiResponse<*> = ApiResponse.badRequest<Any?>(
            (if (e.message != null) e.message else "no error message")!!,
            null
        )

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .header("Content-Type", "application/json")
            .body<ApiResponse<*>?>(response)
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ApiResponse<*>?> {
        val response: ApiResponse<*> = ApiResponse.internalServerError<Any?>(
            (if (e.message != null) e.message else "no error message")!!,
            null
        )

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .header("Content-Type", "application/json")
            .body<ApiResponse<*>?>(response)
    }
}