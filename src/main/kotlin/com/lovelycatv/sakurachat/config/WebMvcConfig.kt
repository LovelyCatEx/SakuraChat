/*
 * Copyright 2025 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */
package com.lovelycatv.sakurachat.config

import com.lovelycatv.sakurachat.interceptor.ControllerLoggingInterceptor
import jakarta.annotation.Resource
import org.springframework.context.annotation.Configuration
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
}