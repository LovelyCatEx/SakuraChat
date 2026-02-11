/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.controller.manager.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

class ManagerCreateThirdPartyAccountDTO(
    @field:NotBlank(message = "account id cannot be empty")
    @field:Size(max = 64, message = "account id length should not be greater than 64")
    val accountId: String,
    @field:NotBlank(message = "nickname cannot be empty")
    @field:Size(max = 256, message = "nickname length should not be greater than 256")
    val nickname: String,
    val platform: Int
)
