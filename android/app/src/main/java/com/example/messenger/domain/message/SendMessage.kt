package com.example.messenger.domain.message

import android.content.Context
import androidx.room.withTransaction
import androidx.work.WorkManager
import com.example.messenger.data.local.db.MessengerDb
import com.example.messenger.data.local.db.entities.relation.MessageWithAuthor
import com.example.messenger.data.local.db.entities.relation.toDto
import com.example.messenger.data.network.models.message.dto.MessageDto
import com.example.messenger.data.network.models.message.dto.toDto
import com.example.messenger.data.workers.SendMessageWorker
import com.example.messenger.di.IoDispatcher
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SendMessage @Inject constructor(
    @ApplicationContext
    context:Context,
    private val db: MessengerDb,
    private val generateMessage: GenerateMessage,
    @IoDispatcher
    private val dispatcher: CoroutineDispatcher,
) {

    private val workManager = WorkManager.getInstance(context)

    suspend operator fun invoke(dto: MessageDto, images:List<String>) = withContext(dispatcher){
        val massage = generateMessage.invoke(dto, images)
        saveMessage(massage)
        runWorker(massage.toDto(), images)
    }

    private fun runWorker(dto: MessageDto, images: List<String>){
        workManager.enqueue(
            SendMessageWorker
                .createOneTimeWorkRequest(dto, images)
        )
    }

    private suspend fun saveMessage(msg:MessageWithAuthor){
        db.withTransaction {
            db.getUserDao().insertItem(msg.author)
            db.getMessagesDao().insertItem(msg.message)
        }
    }

}