/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.core.im.thirdparty

import com.lovelycatv.sakurachat.core.im.message.AbstractMessage
import com.lovelycatv.sakurachat.types.ThirdPartyPlatform

interface IThirdPartyIMAccessor<I: Any, T: Any> {
    fun getPlatform(): ThirdPartyPlatform

    /**
     * Send a private message from sender to target.
     *
     * @param invoker Sender
     * @param target Whom the message will be sent to.
     *               Please use [resolveTargetByPlatformAccountId] to get a valid target
     *               unless you are sure that what you do.
     * @param message [AbstractMessage]
     * @return Send success returns true
     */
    suspend fun sendPrivateMessage(invoker: I, target: T, message: AbstractMessage): Boolean

    /**
     * Resolve a valid target.
     *
     * @param platformAccountId normally comes from [com.lovelycatv.sakurachat.core.SakuraChatMessageExtra.platformAccountId]
     * @return
     */
    suspend fun resolveTargetByPlatformAccountId(platformAccountId: String): T?
}