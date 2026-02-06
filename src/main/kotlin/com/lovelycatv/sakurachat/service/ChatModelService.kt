package com.lovelycatv.sakurachat.service

import com.lovelycatv.sakurachat.entity.aggregated.AggregatedChatModelEntity
import com.lovelycatv.sakurachat.repository.ChatModelRepository

interface ChatModelService : BaseService<ChatModelRepository> {
    fun getAggregatedChatModelEntityById(id: Long): AggregatedChatModelEntity?
}