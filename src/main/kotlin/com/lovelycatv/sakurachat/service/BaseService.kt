/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.service

import org.springframework.data.jpa.repository.JpaRepository

interface BaseService<R: JpaRepository<*, *>> {
    fun getRepository(): R
}