/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.thirdparty

import com.lovelycatv.sakurachat.types.ThirdPartyPlatform
import com.lovelycatv.vertex.log.logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.springframework.beans.factory.getBeansOfType
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component

@Component
class ThirdPartyMessageDispatcherManager(
    private val applicationContext: ApplicationContext
) {
    private val logger = logger()

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    fun dispatch(platform: ThirdPartyPlatform, vararg args: Any?) {
        val dispatcher = applicationContext
            .getBeansOfType<AbstractThirdPartyMessageDispatcher>()
            .values
            .firstOrNull {
                it.platform == platform
            }
            ?: throw IllegalStateException("No third party message dispatcher found for ${platform.name}")

        coroutineScope.launch {
            dispatcher.handle(*args)
        }
    }
}