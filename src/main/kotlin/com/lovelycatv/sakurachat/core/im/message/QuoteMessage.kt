/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.core.im.message

import com.lovelycatv.sakurachat.core.ExtraBody

class QuoteMessage(
    sequence: Long,
    extraBody: ExtraBody?,
    val message: AbstractMessage
) : AbstractMessage(MessageType.QUOTE, sequence, extraBody) {
    override fun isEmpty(): Boolean {
        return this.message.isEmpty()
    }
}