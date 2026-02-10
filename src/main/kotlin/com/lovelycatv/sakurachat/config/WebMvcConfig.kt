/*
 * Copyright 2025 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */
package com.lovelycatv.sakurachat.config

import com.lovelycatv.sakurachat.interceptor.ControllerLoggingInterceptor
import com.lovelycatv.sakurachat.types.UserAuthentication
import com.lovelycatv.sakurachat.utils.JwtUtil
import jakarta.annotation.Resource
import jakarta.servlet.http.HttpServletRequest
import org.springframework.context.annotation.Configuration
import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebMvcConfig : WebMvcConfigurer {
    @Resource
    private lateinit var controllerLoggingInterceptor: ControllerLoggingInterceptor

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry
            .addInterceptor(controllerLoggingInterceptor)
            .addPathPatterns("/**")
    }

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(object : HandlerMethodArgumentResolver {
            override fun supportsParameter(parameter: MethodParameter): Boolean {
                return parameter.parameterType == UserAuthentication::class.java
            }

            override fun resolveArgument(
                parameter: MethodParameter,
                mavContainer: ModelAndViewContainer?,
                webRequest: NativeWebRequest,
                binderFactory: WebDataBinderFactory?
            ): Any? {
                val request = webRequest.nativeRequest as? HttpServletRequest?
                    ?: return null
                val token: String = request.getHeader("Authorization")
                    ?: return null

                val claims = try {
                    JwtUtil.parseToken("jwtSignKey", token)
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                } ?: return null

                return UserAuthentication(
                    userId = claims.get("userId", String::class.java).toLong(),
                    username = claims.subject
                )
            }

        })
    }
}