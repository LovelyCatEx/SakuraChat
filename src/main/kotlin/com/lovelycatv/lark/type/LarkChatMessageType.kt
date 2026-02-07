/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.lark.type

/**
 * https://open.feishu.cn/document/server-docs/im-v1/message/events/receive
 *
 */
enum class LarkChatMessageType(val typeName: String) {
    P2P("p2p"),
    GROUP("group");

    companion object {
        fun getByTypeName(typeName: String): LarkChatMessageType? {
            return entries.find { it.typeName == typeName }
        }
    }
}