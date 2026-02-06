/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.core

import com.lovelycatv.sakurachat.types.ThirdPartyPlatform
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

class SakuraChatMessageExtra(platform: ThirdPartyPlatform) : ExtraBody() {
    companion object {
        const val KEY_PLATFORM = "platform"

        @OptIn(ExperimentalContracts::class)
        fun isCapable(body: ExtraBody): Boolean {
            contract {
                returns(true) implies (body is SakuraChatMessageExtra)
            }

            return body.contains(KEY_PLATFORM) && body.getInt(KEY_PLATFORM)!! >= 0
        }
    }

    init {
        this[KEY_PLATFORM] = platform.platformId
    }

    fun getPlatform(): ThirdPartyPlatform {
        return ThirdPartyPlatform.getByPlatformId(this.getInt(KEY_PLATFORM)!!)!!
    }
}