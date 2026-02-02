/*
 * Copyright 2025 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */
package com.lovelycatv.sakurachat.config

import com.lovelycatv.sakurachat.utils.SnowIdGenerator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SnowIdGeneratorConfig {
    @Bean
    fun snowIdGenerator() = SnowIdGenerator(
        0,
        41,
        5,
        5,
        12,
        0,
        0,
        0,
        0
    )
}