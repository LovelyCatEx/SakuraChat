/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.thirdparty

import com.lovelycatv.sakurachat.types.ThirdPartyPlatform

abstract class AbstractThirdPartyMessageDispatcher(
    val platform: ThirdPartyPlatform
) {
    protected abstract suspend fun doHandle(vararg args: Any?): Boolean
    suspend fun handle(vararg args: Any?): Boolean {
        return this.doHandle(*args)
    }

    protected inline fun <reified I: Any> firstInstanceOrNull(vararg args: Any?): I? {
        return args.firstOrNull { it is I } as? I?
    }
}