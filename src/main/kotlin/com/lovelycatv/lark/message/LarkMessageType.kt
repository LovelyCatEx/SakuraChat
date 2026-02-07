/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.lark.message

/**
 * https://open.feishu.cn/document/server-docs/im-v1/message-content-description/message_content#c9e08671
 *
 */
enum class LarkMessageType(val typeName: String) {
    TEXT("text"),
    POST("post"),
    IMAGE("image"),
    FILE("file"),
    FOLDER("folder"),
    AUDIO("audio"),
    MEDIA("media"),
    STICKER("sticker"),
    INTERACTIVE("interactive"),
    RED_PACK("hongbao"),
    SHARE_CALENDAR_EVENT("share_calendar_event"),
    SHARE_CHAT("share_chat"),
    SHARE_USER("share_user"),
    SYSTEM("system"),
    LOCATION("location"),
    VOICE_CHAT("voice_chat"),
    VIDEO_CHAT("video_chat"),
    TODO("todo"),
    VOTE("vote"),
    MERGE_FORWARD("merge_forward");

    companion object {
        fun getTypeByName(name: String): LarkMessageType? {
            return entries.firstOrNull { it.typeName.equals(name, true) }
        }
    }
}