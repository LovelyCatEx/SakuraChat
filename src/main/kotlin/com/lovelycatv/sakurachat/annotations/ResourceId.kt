/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.annotations

import jakarta.validation.constraints.Min

@Min(1, message = "resource id should not be less than 1")
annotation class ResourceId