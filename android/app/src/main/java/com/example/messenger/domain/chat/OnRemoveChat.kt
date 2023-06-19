package com.example.messenger.domain.chat

import androidx.room.withTransaction
import com.example.messenger.data.local.db.MessengerDb
import com.example.messenger.data.network.models.socket.chat.RemoveChatMsg
import com.example.messenger.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class OnRemoveChat @Inject constructor(
    private val db: MessengerDb,
    @IoDispatcher
    private val dispatcher: CoroutineDispatcher,
) {

    suspend operator fun invoke(chat: RemoveChatMsg) = withContext(dispatcher){
        db.withTransaction {
            db.getMessagesDao().deleteAllItemsByChat(chatId = chat.chatId)
            db.getChatDao().deleteById(chat.chatId)
        }
    }
}