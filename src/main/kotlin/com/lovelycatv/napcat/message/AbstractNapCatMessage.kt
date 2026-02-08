/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.napcat.message

import com.fasterxml.jackson.annotation.JsonIgnore
import com.lovelycatv.napcat.types.NapCatMessageType

abstract class AbstractNapCatMessage(val messageType: NapCatMessageType) {
    @JsonIgnore
    abstract fun isEmpty(): Boolean

    @JsonIgnore
    fun isNotEmpty(): Boolean = !this.isEmpty()
}