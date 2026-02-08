/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.core.im.message

import com.fasterxml.jackson.annotation.*
import com.lovelycatv.sakurachat.core.ExtraBody

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "type",
    visible = true
)
@JsonSubTypes(
    JsonSubTypes.Type(value = TextMessage::class, name = "TEXT"),
    JsonSubTypes.Type(value = QuoteMessage::class, name = "QUOTE"),
    JsonSubTypes.Type(value = JsonMessage::class, name = "JSON"),
    JsonSubTypes.Type(value = ChainMessage::class, name = "CHAIN"),
)
abstract class AbstractMessage @JsonCreator constructor(
    @field:JsonProperty("type")
    val type: MessageType,
    @field:JsonProperty("sequence")
    val sequence: Long = 0,
    @field:JsonProperty("extraBody")
    val extraBody: ExtraBody? = null
) {
    @JsonIgnore
    abstract fun isEmpty(): Boolean

    @JsonIgnore
    fun isNotEmpty(): Boolean = !this.isEmpty()

    @JsonIgnore
    abstract fun isAtomic(): Boolean
}