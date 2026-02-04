/*
 * Copyright 2025 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */
package com.lovelycatv.sakurachat.config

import com.lovelycatv.sakurachat.filter.AuthorizationFilter
import com.lovelycatv.sakurachat.filter.CustomLoginFilter
import jakarta.annotation.Resource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher
import org.springframework.security.web.util.matcher.RequestMatcher

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig {
    @Resource
    private lateinit var authenticationConfiguration: AuthenticationConfiguration

    @Bean
    @Throws(Exception::class)
    fun defaultSecurityFilterChain(http: HttpSecurity): SecurityFilterChain? {
        val requestMatchers: MutableList<RequestMatcher> = ArrayList()
        requestMatchers.add(PathPatternRequestMatcher.withDefaults().matcher("/user/login"))
        requestMatchers.add(PathPatternRequestMatcher.withDefaults().matcher("/user/register"))
        requestMatchers.add(PathPatternRequestMatcher.withDefaults().matcher("/ws/v1/napcat"))

        http.authorizeHttpRequests { registry ->
            requestMatchers.forEach {
                registry.requestMatchers(it).permitAll()
            }

            registry.anyRequest().authenticated()
        }

        http.sessionManagement { it: SessionManagementConfigurer<HttpSecurity?> ->
            it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        }

        http.httpBasic { obj: HttpBasicConfigurer<HttpSecurity> -> obj.disable() }

        http.csrf { obj: CsrfConfigurer<HttpSecurity> -> obj.disable() }

        http.cors { obj: CorsConfigurer<HttpSecurity> -> obj.disable() }

        http.addFilterBefore(
            CustomLoginFilter(
                "/user/login",
                authenticationConfiguration.authenticationManager
            ),
            UsernamePasswordAuthenticationFilter::class.java
        )
        http.addFilterBefore(
            AuthorizationFilter(requestMatchers),
            UsernamePasswordAuthenticationFilter::class.java
        )

        return http.build()
    }

    @Bean
    fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration): AuthenticationManager? {
        return authenticationConfiguration.authenticationManager
    }
    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()
}