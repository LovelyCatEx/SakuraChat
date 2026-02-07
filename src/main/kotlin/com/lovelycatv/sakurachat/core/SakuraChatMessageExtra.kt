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

data class SakuraChatMessageExtra(
    val platform: ThirdPartyPlatform,
    val platformAccountId: String,
    val platformInvoker: Any
) : ExtraBody() {
    companion object {
        const val KEY_PLATFORM = "platform"
        const val KEY_PLATFORM_ACCOUNT_ID = "platform_account_id"
        const val KEY_PLATFORM_INVOKER = "platform_invoker"

        @OptIn(ExperimentalContracts::class)
        fun isCapable(body: ExtraBody?): Boolean {
            contract {
                returns(true) implies (body is SakuraChatMessageExtra)
            }

            return body != null && body.contains(KEY_PLATFORM) && body.getInt(KEY_PLATFORM)!! >= 0
        }
    }

    init {
        this[KEY_PLATFORM] = platform.platformId
        this[KEY_PLATFORM_ACCOUNT_ID] = platformAccountId
        this[KEY_PLATFORM_INVOKER] = platformInvoker.toString()
    }

    fun getPlatformType(): ThirdPartyPlatform {
        return ThirdPartyPlatform.getByPlatformId(this.getInt(KEY_PLATFORM)!!)!!
    }
}