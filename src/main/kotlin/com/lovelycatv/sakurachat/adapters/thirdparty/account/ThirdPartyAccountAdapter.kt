/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.adapters.thirdparty.account

import com.lovelycatv.sakurachat.types.ThirdPartyPlatform

interface ThirdPartyAccountAdapter<I: Any> {
    fun getPlatform(): ThirdPartyPlatform

    fun getThirdPartyAccountClass(): Class<I>

    fun getAccountId(thirdPartyAccount: I): String

    fun getNickName(thirdPartyAccount: I): String
}