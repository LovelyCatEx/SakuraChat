/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.constants

import com.lovelycatv.sakurachat.types.UserRoleType

object SystemRolePermissions {
    const val PERMISSION_PUBLIC = "hasAnyAuthority('${UserRoleType.ROLE_ROOT}', '${UserRoleType.ROLE_ADMIN}', '${UserRoleType.ROLE_USER}')"
    const val PERMISSION_ROOT_ADMIN = "hasAnyAuthority('${UserRoleType.ROLE_ROOT}', '${UserRoleType.ROLE_ADMIN}')"
    const val PERMISSION_ROOT_ONLY = "hasAnyAuthority('${UserRoleType.ROLE_ROOT}')"
}