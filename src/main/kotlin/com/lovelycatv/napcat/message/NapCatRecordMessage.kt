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

class NapCatRecordMessage(
    val path: String,
    val file: String,
    val url: String,
    @field:JsonProperty("file_size")
    val fileSize: String,
) : AbstractNapCatMessage(NapCatMessageType.RECORD) {
    override fun isEmpty(): Boolean {
        return this.path.isEmpty()
    }
}