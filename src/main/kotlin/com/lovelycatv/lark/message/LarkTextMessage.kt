/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.lark.message

/**
 * Lark Text Message
 *
 * @property text text
 */
class LarkTextMessage(
    val text: String
) : AbstractLarkMessage(LarkMessageType.TEXT)