/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.napcat.types

import com.mikuac.shiro.enums.MsgTypeEnum

enum class NapCatMessageType(val typeName: String) {
    AT("at"),
    TEXT("text"),
    FACE("face"),
    M_FACE("mface"),
    MARKET_FACE("marketface"),
    BASKETBALL("basketball"),
    RECORD("record"),
    VIDEO("video"),
    RPS("rps"),
    NEW_RPS("new_rps"),
    DICE("dice"),
    NEW_DICE("new_dice"),
    SHAKE("shake"),
    ANONYMOUS("anonymous"),
    SHARE("share"),
    CONTACT("contact"),
    LOCATION("location"),
    MUSIC("music"),
    IMAGE("image"),
    REPLY("reply"),
    RED_BAG("redbag"),
    POKE("poke"),
    GIFT("gift"),
    FORWARD("forward"),
    MARKDOWN("markdown"),
    KEYBOARD("keyboard"),
    NODE("node"),
    XML("xml"),
    JSON("json"),
    CARD_IMAGE("cardimage"),
    TTS("tts"),
    LONG_MSG("longmsg"),
    UNKNOWN("unknown");

    companion object {
        fun fromTypeName(typeName: String): NapCatMessageType? {
            return entries.find { it.typeName == typeName }
        }

        fun fromMsgTypeEnum(msgTypeEnum: MsgTypeEnum): NapCatMessageType? {
            return fromTypeName(msgTypeEnum.name)
        }
    }
}