package com.example.messenger.data.repository.chat

import androidx.paging.PagingData
import com.example.messenger.common.GenericResponse
import com.example.messenger.data.local.db.entities.relation.ChatWithMessageAndUser
import com.example.messenger.data.network.models.chat.create.CreateChatResponse
import kotlinx.coroutines.flow.Flow

interface ChatRepository {

    fun getChats(): Flow<PagingData<ChatWithMessageAndUser>>

    suspend fun createChat(friendUserId:String):GenericResponse<CreateChatResponse>

    suspend fun removeChat(chatId:String):GenericResponse<Unit>
}