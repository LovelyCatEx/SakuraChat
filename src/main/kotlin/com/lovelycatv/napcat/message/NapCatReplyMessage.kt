/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.napcat.message

import com.lovelycatv.napcat.types.NapCatMessageType

class NapCatReplyMessage(
    val id: String,
) : AbstractNapCatMessage(NapCatMessageType.REPLY) {
    override fun isEmpty(): Boolean {
        return id.isEmpty()
    }
}