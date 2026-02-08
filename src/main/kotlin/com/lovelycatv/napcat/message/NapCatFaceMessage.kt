/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.napcat.message

import com.lovelycatv.napcat.types.NapCatMessageType

class NapCatFaceMessage(
    val id: String,
    val raw: Raw
) : AbstractNapCatMessage(NapCatMessageType.FACE) {
    override fun isEmpty(): Boolean {
        return this.raw.faceText.isEmpty()
    }

    data class Raw(
        val faceIndex: Int,
        val faceText: String,
        val faceType: Int
    )
}