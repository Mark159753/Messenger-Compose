package com.example.messenger.data.repository.messages

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.room.withTransaction
import com.example.messenger.common.GenericResponse
import com.example.messenger.common.PAGING_SIZE
import com.example.messenger.common.exstantions.toMultiPartBody
import com.example.messenger.common.exstantions.toPartMap
import com.example.messenger.data.local.db.MessengerDb
import com.example.messenger.data.local.db.entities.MessageEntity
import com.example.messenger.data.local.db.entities.relation.MessageWithAuthor
import com.example.messenger.data.local.db.entities.relation.toEntityWithAuthor
import com.example.messenger.data.network.ApiService
import com.example.messenger.data.network.adapter.NetworkResponse
import com.example.messenger.data.network.models.message.MessageResponse
import com.example.messenger.data.network.models.message.dto.MessageDto
import com.example.messenger.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class MessagesRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val db: MessengerDb,
    @IoDispatcher
    private val dispatcher: CoroutineDispatcher
):MessagesRepository {

    override fun getMessages(chatId: String): Flow<PagingData<MessageWithAuthor>> {
        Log.d("MessagesRepositoryImpl", "Chat_ID: $chatId")

        val pagingSourceFactory = { db.getMessagesDao().getMessagesByChat(chatId)}

        @OptIn(ExperimentalPagingApi::class)
            return Pager(
                config = PagingConfig(pageSize = PAGING_SIZE, enablePlaceholders = false),
                remoteMediator = MessagesRemoteMediator(
                    chatId,
                    db,
                    apiService
                ),
                pagingSourceFactory = pagingSourceFactory
            ).flow
    }

    override suspend fun sendMessage(
        dto: MessageDto,
        images: List<File>
    ): GenericResponse<MessageResponse> = withContext(dispatcher){
        return@withContext when(val res = apiService.sendMessage(dto.toPartMap(), images.map { it.toMultiPartBody("images", "image/*") })){
            is NetworkResponse.ApiError -> res
            is NetworkResponse.Success -> {
                saveMessage(res.body.toEntityWithAuthor())
                res
            }
        }
    }

    private suspend fun saveMessage(msg: MessageWithAuthor){
        db.withTransaction {
            db.getUserDao().insertItem(msg.author)
            db.getMessagesDao().insertItem(msg.message)
        }
    }
}