/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.napcat.message

import com.fasterxml.jackson.annotation.JsonProperty
import com.lovelycatv.napcat.types.NapCatMessageType

class NapCatImageMessage(
    val summary: String,
    val file: String,
    @field:JsonProperty("sub_type")
    val subType: String,
    val url: String,
    val fileSize: String
) : AbstractNapCatMessage(NapCatMessageType.IMAGE) {
    override fun isEmpty(): Boolean {
        return this.url.isEmpty()
    }
}