/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.types

enum class PointsChangesReason(val reasonId: Int) {
    REGISTER(0),
    AGENT_CHAT_COMPLETION(1),
    CHARGE(2),
    ADMIN(3);

    companion object {
        fun getByReasonId(reasonId: Int): PointsChangesReason? {
            return entries.find { it.reasonId == reasonId }
        }
    }
}