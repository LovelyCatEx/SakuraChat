/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.napcat.message

import com.lovelycatv.napcat.types.NapCatMessageType

class NapCatPokeMessage(
    val id: String,
    val type: String,
) : AbstractNapCatMessage(NapCatMessageType.POKE) {
    override fun isEmpty(): Boolean {
        return false
    }
}