/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.core.im.message

import com.lovelycatv.sakurachat.core.ExtraBody
import com.lovelycatv.sakurachat.types.ThirdPartyPlatform

interface IMessageAdapter<I> {
    fun getPlatform(): ThirdPartyPlatform

    fun getInputMessageClass(): Class<I>

    fun transform(input: I, extraBody: ExtraBody): AbstractMessage
}