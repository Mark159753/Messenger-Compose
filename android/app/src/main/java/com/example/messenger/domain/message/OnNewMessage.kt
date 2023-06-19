package com.example.messenger.domain.message

import androidx.room.withTransaction
import com.example.messenger.data.local.db.MessengerDb
import com.example.messenger.data.local.db.entities.ChatEntity
import com.example.messenger.data.local.db.entities.toEntity
import com.example.messenger.data.local.proto.user.UserLocalDataSource
import com.example.messenger.data.network.models.message.MessageResponse
import com.example.messenger.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

import javax.inject.Inject

class OnNewMessage @Inject constructor(
    private val db: MessengerDb,
    private val userLocalDataSource: UserLocalDataSource,
    @IoDispatcher
    private val dispatcher: CoroutineDispatcher,
) {

    suspend operator fun invoke(msg:MessageResponse){
        withContext(dispatcher){
            val me = userLocalDataSource.userPreferencesFlow.first()
            if (msg.author.id == me.id) return@withContext
            db.withTransaction {
                db.getUserDao().insertItem(msg.author.toEntity())
                db.getMessagesDao().insertItem(msg.toEntity())
                db.getChatDao().insertItem(
                    ChatEntity(
                        id = msg.chatId,
                        messageId = msg.id,
                        userId = msg.author.id
                    )
                )
            }
        }
    }
}