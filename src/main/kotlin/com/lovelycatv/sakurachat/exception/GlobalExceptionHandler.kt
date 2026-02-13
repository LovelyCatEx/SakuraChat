/*
 * Copyright 2025 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */
package com.lovelycatv.sakurachat.exception

import com.lovelycatv.sakurachat.request.ApiResponse
import com.lovelycatv.vertex.log.logger
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authorization.AuthorizationDeniedException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    private val logger = logger()

    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(e: BusinessException): ResponseEntity<ApiResponse<*>?> {
        val response: ApiResponse<*> = ApiResponse.badRequest<Any?>(
            (if (e.message != null) e.message else "no error message")!!,
            null
        )

        return ResponseEntity
            .status(HttpStatus.OK)
            .header("Content-Type", "application/json")
            .body<ApiResponse<*>?>(response)
    }

    @ExceptionHandler(AuthorizationDeniedException::class)
    fun handleAuthorizationDeniedException(e: AuthorizationDeniedException): ResponseEntity<ApiResponse<*>?> {
        val response: ApiResponse<*> = ApiResponse.forbidden<Any?>(
            (if (e.message != null) e.message else "no error message")!!,
            null
        )

        return ResponseEntity
            .status(HttpStatus.OK)
            .header("Content-Type", "application/json")
            .body<ApiResponse<*>?>(response)
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ApiResponse<*>?> {
        if (e is BusinessException) {
            return this.handleBusinessException(e)
        } else if (e is AuthorizationDeniedException) {
            return this.handleAuthorizationDeniedException(e)
        }

        val response: ApiResponse<*> = ApiResponse.internalServerError<Any?>(
            (if (e.message != null) e.message else "no error message")!!,
            null
        )

        logger.error("An error occurred while processing request", e)

        return ResponseEntity
            .status(HttpStatus.OK)
            .header("Content-Type", "application/json")
            .body<ApiResponse<*>?>(response)
    }
}