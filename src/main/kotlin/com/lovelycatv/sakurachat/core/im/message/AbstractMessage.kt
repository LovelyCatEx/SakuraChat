/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.core.im.message

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.lovelycatv.sakurachat.core.ExtraBody

abstract class AbstractMessage @JsonCreator constructor(
    @field:JsonProperty("type")
    val type: MessageType,
    @field:JsonProperty("sequence")
    val sequence: Long,
    @field:JsonProperty("extraBody")
    val extraBody: ExtraBody?
)