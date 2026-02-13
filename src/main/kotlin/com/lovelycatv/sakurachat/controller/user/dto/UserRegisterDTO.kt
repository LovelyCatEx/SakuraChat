/*
 * Copyright 2025 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */
package com.lovelycatv.sakurachat.controller.user.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class UserRegisterDTO(
    @field:NotBlank(message = "用户名不能为空")
    @field:Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含数字、字母和下划线")
    @field:Size(max = 64, message = "用户名长度不能超过64个字符")
    val username: String,
    @field:NotBlank(message = "密码不能为空")
    @field:Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9]).{8,}$", message = "密码至少8位，且包含数字和字母")
    @field:Size(max = 128, message = "密码长度不能超过128个字符")
    val password: String,
    @field:NotBlank(message = "邮箱不能为空")
    @field:Email(message = "邮箱格式不正确")
    @field:Size(max = 256, message = "邮箱长度不能超过256个字符")
    val email: String,
    @field:NotBlank(message = "验证码不能为空")
    val emailCode: String
)