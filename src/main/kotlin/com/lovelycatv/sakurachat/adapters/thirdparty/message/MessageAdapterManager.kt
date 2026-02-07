/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.adapters.thirdparty.message

import com.lovelycatv.sakurachat.core.im.message.IMessageAdapter
import com.lovelycatv.sakurachat.types.ThirdPartyPlatform
import org.springframework.beans.factory.getBeansOfType
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component

@Component
class MessageAdapterManager(
    private val applicationContext: ApplicationContext
) {
    fun getAdapter(platform: ThirdPartyPlatform, inputClazz: Class<out Any>): IMessageAdapter<Any>? {
        return applicationContext
            .getBeansOfType<IMessageAdapter<Any>>()
            .values.firstOrNull {
                it.getPlatform() == platform && it.getInputMessageClass().isAssignableFrom(inputClazz)
            }
    }
}