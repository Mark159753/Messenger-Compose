package com.example.messenger.data.repository.messages

import androidx.datastore.core.IOException
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.messenger.data.local.db.MessengerDb
import com.example.messenger.data.local.db.entities.MessageEntity
import com.example.messenger.data.local.db.entities.REMOTE_KEY_MESSAGES
import com.example.messenger.data.local.db.entities.RemoteKeyEntity
import com.example.messenger.data.local.db.entities.relation.MessageWithAuthor
import com.example.messenger.data.local.db.entities.toEntity
import com.example.messenger.data.network.ApiService
import com.example.messenger.data.network.adapter.NetworkResponse
import com.example.messenger.data.network.models.message.MessageResponse

@OptIn(ExperimentalPagingApi::class)
class MessagesRemoteMediator(
    private val chatId:String,
    private val db: MessengerDb,
    private val apiService: ApiService
): RemoteMediator<Int, MessageWithAuthor>() {

    private val userDao = db.getUserDao()
    private val messagesDao = db.getMessagesDao()
    private val remoteKeysDao = db.getRemoteKeysDao()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, MessageWithAuthor>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: 1
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                remoteKeys?.prevKey ?: return MediatorResult.Success(endOfPaginationReached = true)
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                remoteKeys?.nextKey ?: 1
            }
        }
        return when(val apiResponse = apiService.getAllMessages(chatId = chatId, size = state.config.pageSize, page)){
            is NetworkResponse.ApiError -> MediatorResult.Error(IOException(apiResponse.body?.message ?: apiResponse.error?.message))
            is NetworkResponse.Success -> saveData(apiResponse.body, loadType, page)
        }
    }

    private suspend fun saveData(items:List<MessageResponse>, loadType:LoadType, page: Int): MediatorResult {
        val endOfPaging = items.isEmpty()

        db.withTransaction {
            if (loadType == LoadType.REFRESH){
                remoteKeysDao.deleteAllByType(REMOTE_KEY_MESSAGES)
                messagesDao.deleteAllItemsByChat(chatId)
            }
            val prevKey = if (page == 1) null else page - 1
            val nextKey = if (endOfPaging) null else page + 1
            val key = items.map {
                RemoteKeyEntity(it.id, prevKey, nextKey, REMOTE_KEY_MESSAGES)
            }
            remoteKeysDao.insertAllItems(key)
            userDao.insertAllItems(items.map { it.author.toEntity() })
            messagesDao.insertAllItems(items.map { it.toEntity() })
        }

        return MediatorResult.Success(endOfPaginationReached = endOfPaging)
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, MessageWithAuthor>): RemoteKeyEntity? {
        return state.pages.lastOrNull() { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { item ->
                remoteKeysDao.getItemById(item.message.id)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, MessageWithAuthor>): RemoteKeyEntity? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { item ->
                remoteKeysDao.getItemById(item.message.id)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, MessageWithAuthor>): RemoteKeyEntity? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.message?.id?.let { itemId ->
                remoteKeysDao.getItemById(itemId)
            }
        }
    }
}