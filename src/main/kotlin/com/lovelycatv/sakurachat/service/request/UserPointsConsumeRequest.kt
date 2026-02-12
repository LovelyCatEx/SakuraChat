/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.service.request

import com.lovelycatv.sakurachat.types.DatabaseTableType
import com.lovelycatv.sakurachat.types.PointsChangesReason

data class UserPointsConsumeRequest(
    val reason: PointsChangesReason,
    val delta: Long,
    val associations: List<Association> = listOf()
) {
    data class Association(
        val tableType: DatabaseTableType,
        val id: Long
    )
}
