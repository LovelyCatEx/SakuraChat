/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.adapters.thirdparty.group

import com.lovelycatv.sakurachat.types.ThirdPartyPlatform
import org.springframework.beans.factory.getBeansOfType
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component

@Component
class ThirdPartyGroupAdapterManager(
    private val applicationContext: ApplicationContext
) {
    fun getAdapters(platform: ThirdPartyPlatform, inputClazz: Class<out Any>): List<IThirdPartyGroupAdapter<Any>> {
        return applicationContext
            .getBeansOfType<IThirdPartyGroupAdapter<Any>>()
            .values.filter {
                it.getPlatform() == platform && it.getThirdPartyGroupClass().isAssignableFrom(inputClazz)
            }
    }
}