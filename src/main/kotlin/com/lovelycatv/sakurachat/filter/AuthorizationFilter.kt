/*
 * Copyright 2025 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */
package com.lovelycatv.sakurachat.filter

import com.fasterxml.jackson.databind.ObjectMapper
import com.lovelycatv.sakurachat.request.ApiResponse
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.SignatureException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.util.matcher.RequestMatcher
import org.springframework.web.filter.GenericFilterBean

class AuthorizationFilter(
    private val permitAllMatchers: Iterable<RequestMatcher>
) : GenericFilterBean() {

    private companion object {
        val objectMapper = ObjectMapper()
        const val JWT_SIGN_KEY = "jwtSignKey"
        const val BEARER_PREFIX = "Bearer"
    }

    override fun doFilter(
        servletRequest: jakarta.servlet.ServletRequest,
        servletResponse: jakarta.servlet.ServletResponse,
        filterChain: FilterChain
    ) {
        val request = servletRequest as HttpServletRequest
        val response = servletResponse as HttpServletResponse

        if (isPermitAllRequest(request)) {
            filterChain.doFilter(request, response)
            return
        }

        val tokenResult = validateToken(request)
        tokenResult.fold(
            onSuccess = { authToken ->
                SecurityContextHolder.getContext().authentication = authToken
                filterChain.doFilter(request, response)
            },
            onFailure = { error ->
                sendErrorResponse(response, error)
            }
        )
    }

    private fun isPermitAllRequest(request: HttpServletRequest): Boolean {
        return permitAllMatchers.any { it.matches(request) }
    }

    private fun validateToken(request: HttpServletRequest): Result<UsernamePasswordAuthenticationToken> = runCatching {
        val tokenStr = request.getHeader("Authorization")
            ?: throw IllegalStateException("Authorization header is missing")

        val cleanToken = tokenStr.replace(BEARER_PREFIX, "").trim()

        val claims = Jwts.parser()
            .setSigningKey(JWT_SIGN_KEY)
            .parseClaimsJws(cleanToken)
            .body

        val username = claims.subject
        val authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(
            claims.get("authorities", String::class.java) ?: ""
        )

        UsernamePasswordAuthenticationToken(username, null, authorities)
    }

    private fun sendErrorResponse(response: HttpServletResponse, error: Throwable) {
        response.contentType = "application/json;charset=utf-8"

        val errorMessage = when (error) {
            is ExpiredJwtException -> "Token expired"
            is SignatureException -> "Token signature verification failed"
            is MalformedJwtException -> "Token malformed"
            is IllegalStateException -> error.message ?: "Token invalid"
            else -> error.message ?: "Authentication failed"
        }

        val statusCode = when (error) {
            is ExpiredJwtException -> HttpServletResponse.SC_UNAUTHORIZED
            else -> HttpServletResponse.SC_UNAUTHORIZED
        }

        response.status = statusCode

        val apiResponse = ApiResponse.unauthorized<Any?>(errorMessage, null)
        objectMapper.writeValue(response.writer, apiResponse)
        response.writer.flush()
    }
}