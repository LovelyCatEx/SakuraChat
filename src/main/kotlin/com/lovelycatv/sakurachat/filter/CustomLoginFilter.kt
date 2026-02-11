/*
 * Copyright 2025 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */
package com.lovelycatv.sakurachat.filter

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.lovelycatv.sakurachat.request.ApiResponse
import com.lovelycatv.sakurachat.request.ApiResponse.Companion.success
import com.lovelycatv.sakurachat.utils.JwtUtil.buildJwtToken
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import java.io.IOException

class CustomLoginFilter(defaultFilterProcessesUrl: String?, authenticationManager: AuthenticationManager?) :
    AbstractAuthenticationProcessingFilter(defaultFilterProcessesUrl, authenticationManager) {
    @Throws(AuthenticationException::class)
    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse?): Authentication? {
        val username = request.getParameter("username")
        val password = request.getParameter("password")
        return getAuthenticationManager().authenticate(UsernamePasswordAuthenticationToken(username, password))
    }

    @Throws(IOException::class)
    override fun successfulAuthentication(
        request: HttpServletRequest?,
        response: HttpServletResponse,
        chain: FilterChain?,
        authResult: Authentication
    ) {
        val authorities = authResult.getAuthorities()

        response.contentType = "application/json;charset=utf-8"
        val userToken = buildJwtToken("jwtSignKey", authorities, authResult, 7 * 1440 * 60 * 1000L)

        val data = LoginSuccessData()
        data.token = userToken
        data.expiresIn = 15 * 60 * 1000L

        response.writer.write(objectMapper.writeValueAsString(success<LoginSuccessData?>(data, "success")))
        response.writer.flush()
        response.writer.close()
    }

    @Throws(IOException::class)
    override fun unsuccessfulAuthentication(
        request: HttpServletRequest?,
        response: HttpServletResponse,
        failed: AuthenticationException
    ) {
        response.contentType = "application/json;charset=utf-8"
        response.writer.write(
            objectMapper.writeValueAsString(
                ApiResponse.unauthorized<Any?>(
                    (if (failed.message != null)
                        failed.message
                    else
                        "username or password incorrect")!!, null
                )
            )
        )
        response.writer.flush()
        response.writer.close()
    }

    @JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE
    )
    class LoginSuccessData {
        @JsonProperty("token")
        var token: String? = null

        @JsonProperty("expiresIn")
        var expiresIn: Long = 0
    }

    companion object {
        private val objectMapper = ObjectMapper()
    }
}