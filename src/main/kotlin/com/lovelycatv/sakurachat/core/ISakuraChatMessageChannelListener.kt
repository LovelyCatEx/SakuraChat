/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.core

import com.lovelycatv.sakurachat.core.im.channel.MessageChannelListener

interface ISakuraChatMessageChannelListener :
    MessageChannelListener<SakuraChatMessageChannel, ISakuraChatMessageChannelMember>