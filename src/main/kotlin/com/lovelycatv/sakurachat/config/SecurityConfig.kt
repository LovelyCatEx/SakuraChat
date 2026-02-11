/*
 * Copyright 2025 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */
package com.lovelycatv.sakurachat.config

import com.lovelycatv.sakurachat.annotations.Unauthorized
import com.lovelycatv.sakurachat.filter.AuthorizationFilter
import com.lovelycatv.sakurachat.filter.CustomLoginFilter
import com.lovelycatv.vertex.log.logger
import jakarta.annotation.Resource
import org.springframework.beans.factory.getBeansWithAnnotation
import org.springframework.context.ApplicationContext
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
import org.springframework.web.bind.annotation.*

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig {
    private val logger = logger()

    @Resource
    private lateinit var authenticationConfiguration: AuthenticationConfiguration
    @Resource
    private lateinit var applicationContext: ApplicationContext

    @Bean
    @Throws(Exception::class)
    fun defaultSecurityFilterChain(http: HttpSecurity): SecurityFilterChain? {
        val requestMatchers: MutableList<RequestMatcher> = ArrayList()
        requestMatchers.add(PathPatternRequestMatcher.withDefaults().matcher("/user/login"))
        requestMatchers.add(PathPatternRequestMatcher.withDefaults().matcher("/user/register"))
        requestMatchers.add(PathPatternRequestMatcher.withDefaults().matcher("/ws/v1/napcat"))
        this.getUnauthorizedEndpointsSimple().forEach {
            requestMatchers.add(
                PathPatternRequestMatcher.withDefaults().matcher(it)
            )
        }

        logger.warn("The following urls will not go through the authorization check:")
        requestMatchers.forEach {
            logger.warn("$it")
        }

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

    fun getUnauthorizedEndpointsSimple(): List<String> {
        val endpoints = mutableListOf<String>()
        val controllerBeans = applicationContext.getBeansWithAnnotation<RestController>()

        controllerBeans.values.forEach { bean ->
            val beanType = bean.javaClass

            val classUnauthorized = beanType.getAnnotation(Unauthorized::class.java)

            val classRequestMapping = beanType.getAnnotation(RequestMapping::class.java)
            val classPathPrefixes = classRequestMapping?.value?.toList() ?: listOf("")

            val a = beanType.declaredMethods

            beanType.declaredMethods.forEach { method ->
                val methodUnauthorized = method.getAnnotation(Unauthorized::class.java)

                if (classUnauthorized != null || methodUnauthorized != null) {
                    val requestMapping = method.getAnnotation(RequestMapping::class.java)
                    val getMapping = method.getAnnotation(GetMapping::class.java)
                    val postMapping = method.getAnnotation(PostMapping::class.java)
                    val putMapping = method.getAnnotation(PutMapping::class.java)
                    val deleteMapping = method.getAnnotation(DeleteMapping::class.java)
                    val patchMapping = method.getAnnotation(PatchMapping::class.java)

                    val methodPaths = when {
                        requestMapping != null -> requestMapping.value.toList()
                        getMapping != null -> getMapping.value.toList()
                        postMapping != null -> postMapping.value.toList()
                        putMapping != null -> putMapping.value.toList()
                        deleteMapping != null -> deleteMapping.value.toList()
                        patchMapping != null -> patchMapping.value.toList()
                        else -> listOf("")
                    }

                    classPathPrefixes.forEach { classPrefix ->
                        methodPaths.forEach { methodPath ->
                            val fullPath = if (classPrefix.endsWith("/") && methodPath.startsWith("/")) {
                                "$classPrefix${methodPath.substring(1)}"
                            } else if (!classPrefix.endsWith("/") && !methodPath.startsWith("/") && methodPath.isNotEmpty()) {
                                "$classPrefix/$methodPath"
                            } else {
                                "$classPrefix$methodPath"
                            }

                            endpoints.add(fullPath)
                        }
                    }
                }
            }
        }

        return endpoints.distinct()
    }
}