/*
 * Copyright 2025 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */
package com.lovelycatv.sakurachat.interceptor

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.lovelycatv.sakurachat.utils.SnowIdGenerator
import com.lovelycatv.vertex.log.logger
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor

@Component
class ControllerLoggingInterceptor(
    private val snowIdGenerator: SnowIdGenerator
) : HandlerInterceptor {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val objectMapper = jacksonObjectMapper()
    private val logger = logger()

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        coroutineScope.launch {
            val requestURI = request.requestURI
            val method = request.method
            val clientIp = request.remoteAddr

            val code = snowIdGenerator.nextId()

            val headers = objectMapper
                .writerWithDefaultPrettyPrinter()
                .withDefaultPrettyPrinter()
                .writeValueAsString(request.headerNames.toList().associateWith { request.getHeader(it) })
                .split("\n")

            logger.info("+ [{}] ===========================================================", code)
            logger.info("+ [{}] Request: {} {}", code, method, requestURI)
            logger.info("+ [{}] Remote: {}", code, clientIp)
            headers.forEachIndexed { index, it ->
                if (index == 0) {
                    logger.info("+ [{}] Headers: {}", code, it)
                } else {
                    logger.info("+ [{}] {}", code, it)
                }
            }
            if (handler is HandlerMethod) {
                logger.info("+ [{}] Handler: {}", code, handler.beanType.canonicalName)
                logger.info("+ [{}] Method: {}", code, handler.method.toString())
            } else {
                logger.info("+ [{}] Handler: {}", code, handler::class.qualifiedName)
            }
            logger.info("+ [{}] ===========================================================", code)

            request.setAttribute("code", code)
            request.setAttribute("startTime", System.currentTimeMillis())
        }

        return true
    }

    override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        ex: Exception?
    ) {
        coroutineScope.launch {
            val requestURI = request.requestURI
            val method = request.method
            val clientIp = request.remoteAddr


            val code = request.getAttribute("code")

            val headers = objectMapper
                .writerWithDefaultPrettyPrinter()
                .withDefaultPrettyPrinter()
                .writeValueAsString(response.headerNames.toList().associateWith { request.getHeader(it) })
                .split("\n")

            logger.info("+ [{}] ===========================================================", code)
            logger.info("- [{}] Request: {} {}", code, method, requestURI)
            logger.info("- [{}] Remote: {}", code, clientIp)
            headers.forEachIndexed { index, it ->
                if (index == 0) {
                    logger.info("+ [{}] Response Headers: {}", code, it)
                } else {
                    logger.info("+ [{}] {}", code, it)
                }
            }
            logger.info("- [{}] Costs: {}ms", code, System.currentTimeMillis() - (request.getAttribute("startTime") as? Long? ?: System.currentTimeMillis()))
            logger.info("+ [{}] ===========================================================", code)
        }
    }
}