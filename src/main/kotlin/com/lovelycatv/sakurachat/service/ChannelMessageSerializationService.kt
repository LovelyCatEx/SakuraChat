/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.service

import com.lovelycatv.sakurachat.core.im.message.AbstractMessage

interface ChannelMessageSerializationService {
    /**
     * Parse the [abstractMessage] to JSON string.
     *
     * [AbstractMessage.sequence] and [AbstractMessage.extraBody] will be aborted.
     *
     * @param abstractMessage [AbstractMessage]
     * @return JSON string of [abstractMessage]
     */
    fun toJSONString(abstractMessage: AbstractMessage): String

    /**
     * Parse the JSON string to an explicit [AbstractMessage] object.
     *
     * [AbstractMessage.sequence] and [AbstractMessage.extraBody] will be aborted.
     *
     * @param jsonString [String]
     * @return subtype of [AbstractMessage]
     */
    fun fromJSONString(jsonString: String): AbstractMessage
}