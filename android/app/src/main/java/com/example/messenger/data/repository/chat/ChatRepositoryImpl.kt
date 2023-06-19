package com.example.messenger.data.repository.chat

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.room.withTransaction
import com.example.messenger.common.PAGING_SIZE
import com.example.messenger.data.local.db.MessengerDb
import com.example.messenger.data.local.db.entities.relation.ChatWithMessageAndUser
import com.example.messenger.data.network.ApiService
import com.example.messenger.data.network.adapter.NetworkResponse
import com.example.messenger.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    @IoDispatcher
    private val dispatcher: CoroutineDispatcher,
    private val db: MessengerDb
):ChatRepository {

    override fun getChats(): Flow<PagingData<ChatWithMessageAndUser>> {
        val pagingSourceFactory = { db.getChatDao().getAllPaging()}

        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(pageSize = PAGING_SIZE, enablePlaceholders = false),
            remoteMediator = ChatRemoteMediator(
                db,
                apiService
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    override suspend fun createChat(friendUserId: String) = withContext(dispatcher) {
        return@withContext apiService.createChat(friendUserId)
    }

    override suspend fun removeChat(chatId: String) = withContext(dispatcher) {
        return@withContext when(val res = apiService.deleteChat(chatId)){
            is NetworkResponse.ApiError -> res
            is NetworkResponse.Success -> {
                db.withTransaction {
                    db.getMessagesDao().deleteAllItemsByChat(chatId)
                    db.getChatDao().deleteById(chatId)
                }
                res
            }
        }
    }
}