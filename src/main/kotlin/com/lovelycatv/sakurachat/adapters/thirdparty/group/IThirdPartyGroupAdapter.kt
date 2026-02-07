/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.adapters.thirdparty.group

import com.lovelycatv.sakurachat.types.ThirdPartyPlatform

interface IThirdPartyGroupAdapter<I: Any> {
    fun getPlatform(): ThirdPartyPlatform

    fun getThirdPartyGroupClass(): Class<I>

    fun getGroupId(thirdPartyGroup: I): String

    fun getGroupName(thirdPartyGroup: I): String
}