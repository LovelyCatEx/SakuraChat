/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.adapters.thirdparty.group

import com.lark.oapi.service.im.v1.model.EventMessage
import com.lovelycatv.lark.type.LarkChatMessageType
import com.lovelycatv.sakurachat.types.ThirdPartyPlatform
import com.lovelycatv.sakurachat.utils.toJSONString
import org.springframework.stereotype.Component

@Component
class LarkEventMessageGroupAdapter : IThirdPartyGroupAdapter<EventMessage> {
    override fun getPlatform(): ThirdPartyPlatform {
        return ThirdPartyPlatform.LARK
    }

    override fun getThirdPartyGroupClass(): Class<EventMessage> {
        return EventMessage::class.java
    }

    override fun getGroupId(thirdPartyGroup: EventMessage): String {
        this.preCheck(thirdPartyGroup)

        return thirdPartyGroup.chatId
    }

    override fun getGroupName(thirdPartyGroup: EventMessage): String {
        this.preCheck(thirdPartyGroup)

        return thirdPartyGroup.chatId
    }

    private fun preCheck(thirdPartyGroup: EventMessage) {
        if (LarkChatMessageType.getByTypeName(thirdPartyGroup.chatType) != LarkChatMessageType.GROUP) {
            throw IllegalArgumentException("The ${EventMessage::class.qualifiedName} is not a group message, " +
                    "event: ${thirdPartyGroup.toJSONString()}"
            )
        }
    }
}