/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.adapters.thirdparty.im

import com.lovelycatv.sakurachat.core.im.thirdparty.IThirdPartyIMAccessor
import com.lovelycatv.sakurachat.types.ThirdPartyPlatform
import com.lovelycatv.vertex.log.logger
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.getBeansOfType
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component

@Component
class ThirdPartyIMAccessorManager(
    private val applicationContext: ApplicationContext
) : InitializingBean {
    private val logger = logger()

    private val accessors: MutableMap<ThirdPartyPlatform, IThirdPartyIMAccessor<Any, Any, Any>> = mutableMapOf()

    fun getAccessor(platform: ThirdPartyPlatform): IThirdPartyIMAccessor<Any, Any, Any>? {
        return accessors[platform]
    }

    fun registerAccessor(accessor: IThirdPartyIMAccessor<Any, Any, Any>) {
        this.accessors[accessor.getPlatform()] = accessor
        logger.info("Registered ${accessor.getPlatform()} IM Accessor: ${accessor::class.qualifiedName}")
    }

    override fun afterPropertiesSet() {
        val accessors = applicationContext.getBeansOfType<IThirdPartyIMAccessor<Any, Any, Any>>()

        logger.info("=".repeat(96))
        accessors.values.forEach {
            registerAccessor(it)
        }
        logger.info("=".repeat(96))
    }
}