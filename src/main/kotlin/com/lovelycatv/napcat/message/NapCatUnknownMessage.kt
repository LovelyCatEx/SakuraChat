/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.napcat.message

import com.lovelycatv.napcat.types.NapCatMessageType

class NapCatUnknownMessage(
    val data: Map<String, String>
) : AbstractNapCatMessage(NapCatMessageType.UNKNOWN) {
    override fun isEmpty(): Boolean {
        return this.data.isEmpty()
    }
}