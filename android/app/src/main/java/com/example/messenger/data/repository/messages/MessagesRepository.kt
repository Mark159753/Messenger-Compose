package com.example.messenger.data.repository.messages

import androidx.paging.PagingData
import com.example.messenger.common.GenericResponse
import com.example.messenger.data.local.db.entities.relation.MessageWithAuthor
import com.example.messenger.data.network.models.message.MessageResponse
import com.example.messenger.data.network.models.message.dto.MessageDto
import kotlinx.coroutines.flow.Flow
import java.io.File

interface MessagesRepository {

    fun getMessages(chatId: String): Flow<PagingData<MessageWithAuthor>>

    suspend fun sendMessage(dto: MessageDto, images: List<File>):GenericResponse<MessageResponse>
}