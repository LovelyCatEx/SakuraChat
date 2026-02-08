/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.thirdparty.napcat

import com.lovelycatv.sakurachat.thirdparty.ThirdPartyMessageDispatcherManager
import com.lovelycatv.sakurachat.types.ThirdPartyPlatform
import com.mikuac.shiro.core.Bot
import com.mikuac.shiro.core.BotPlugin
import com.mikuac.shiro.dto.event.message.GroupMessageEvent
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent
import org.springframework.stereotype.Component

/**
 * https://misakatat.github.io/shiro-docs/
 *
 * @property thirdPartyMessageDispatcherManager
 */
@Component
class NapCatBotPlugin(
    private val thirdPartyMessageDispatcherManager: ThirdPartyMessageDispatcherManager
) : BotPlugin() {
    override fun onPrivateMessage(bot: Bot, event: PrivateMessageEvent): Int {
        thirdPartyMessageDispatcherManager.dispatch(ThirdPartyPlatform.NAPCAT_OICQ, bot, event)

        return super.onPrivateMessage(bot, event)
    }

    override fun onGroupMessage(bot: Bot, event: GroupMessageEvent): Int {
        thirdPartyMessageDispatcherManager.dispatch(ThirdPartyPlatform.NAPCAT_OICQ, bot, event)

        return super.onGroupMessage(bot, event)
    }
}