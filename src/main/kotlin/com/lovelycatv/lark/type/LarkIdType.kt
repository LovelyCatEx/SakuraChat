/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.lark.type

/**
 * To tell the difference between types, see:
 * https://open.feishu.cn/document/platform-overveiw/basic-concepts/user-identity-introduction/introduction
 *
 */
enum class LarkIdType(val typeName: String) {
    APP_ID("app_id"),
    USER_ID("user_id"),
    OPEN_ID("open_id"),
    UNION_ID("union_id"),
    CHAT_ID("chat_id");
}