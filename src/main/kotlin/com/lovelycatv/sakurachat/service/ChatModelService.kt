package com.lovelycatv.sakurachat.service

import com.lovelycatv.sakurachat.controller.manager.dto.ManagerCreateChatModelDTO
import com.lovelycatv.sakurachat.controller.manager.dto.UpdateChatModelDTO
import com.lovelycatv.sakurachat.entity.ChatModelEntity
import com.lovelycatv.sakurachat.entity.aggregated.AggregatedChatModelEntity
import com.lovelycatv.sakurachat.repository.ChatModelRepository
import com.lovelycatv.sakurachat.request.PaginatedResponseData

interface ChatModelService : BaseService<ChatModelRepository, ChatModelEntity, Long> {
    fun getAggregatedChatModelEntityById(id: Long): AggregatedChatModelEntity?

    suspend fun updateChatModel(updateChatModelDTO: UpdateChatModelDTO)

    suspend fun createChatModel(managerCreateChatModelDTO: ManagerCreateChatModelDTO)

    suspend fun search(keyword: String, page: Int, pageSize: Int): PaginatedResponseData<ChatModelEntity>
}