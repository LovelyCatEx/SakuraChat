package com.lovelycatv.sakurachat.service

import com.lovelycatv.sakurachat.entity.ChatModelEntity
import com.lovelycatv.sakurachat.entity.aggregated.AggregatedChatModelEntity
import com.lovelycatv.sakurachat.repository.ChatModelRepository

interface ChatModelService : BaseService<ChatModelRepository, ChatModelEntity, Long> {
    fun getAggregatedChatModelEntityById(id: Long): AggregatedChatModelEntity?
}