/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.adapters

import com.lovelycatv.sakurachat.entity.ThirdPartyAccountEntity
import com.lovelycatv.sakurachat.types.ThirdPartyPlatform

interface ThirdPartyAccountAdapter<I: Any> {
    fun getPlatform(): ThirdPartyPlatform

    fun getThirdPartyAccountClass(): Class<I>

    fun transform(thirdPartyAccount: I): ThirdPartyAccountEntity

    fun safeTransform(thirdPartyAccount: I): ThirdPartyAccountEntity {
        return transform(thirdPartyAccount).apply {
            this.platform = getPlatform().platformId
        }
    }
}