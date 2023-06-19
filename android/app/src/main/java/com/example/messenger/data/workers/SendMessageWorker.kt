package com.example.messenger.data.workers

import android.content.Context
import android.os.Build
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.messenger.common.UNKNOWN_ERROR
import com.example.messenger.data.local.db.MessengerDb
import com.example.messenger.data.network.adapter.NetworkResponse
import com.example.messenger.data.network.models.message.dto.MessageDto
import com.example.messenger.data.repository.messages.MessagesRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.io.File

@HiltWorker
class SendMessageWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val messagesRepository: MessagesRepository,
    private val db: MessengerDb
): CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val dto = MessageDto(
            id = inputData.getString(ID),
            message = inputData.getString(MESSAGE),
            chatId = inputData.getString(CHAT_ID) ?: return Result.failure()
        )

        val images = inputData.getStringArray(IMAGES)?.asList() ?: emptyList()

        return when(val res = messagesRepository.sendMessage(dto, images.map { File(it) })){
            is NetworkResponse.ApiError -> {
                if (res.error != null)
                    Result.retry()
                else{
                    dto.id?.let { id ->
                        db.getMessagesDao().deleteById(id)
                    }
                    Result.failure(
                        workDataOf(ERROR_MSG to (res.body?.message ?: UNKNOWN_ERROR))
                    )
                }
            }
            is NetworkResponse.Success -> Result.success()
        }
    }

    companion object{
        const val ERROR_MSG = "com.example.messenger.data.workers.SendMessageWorker.ERROR_MSG"

        private const val ID = "com.example.messenger.data.workers.SendMessageWorker.ID"
        private const val CHAT_ID = "com.example.messenger.data.workers.SendMessageWorker.CHAT_ID"
        private const val MESSAGE = "com.example.messenger.data.workers.SendMessageWorker.MESSAGE"
        private const val IMAGES = "com.example.messenger.data.workers.SendMessageWorker.IMAGES"

        fun createOneTimeWorkRequest(dto: MessageDto, images:List<String> = emptyList()): OneTimeWorkRequest{
            val data = workDataOf(
                ID to dto.id,
                CHAT_ID to dto.chatId,
                MESSAGE to dto.message,
                IMAGES to images.toTypedArray()
            )
            val worker = OneTimeWorkRequestBuilder<SendMessageWorker>()

            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            worker.apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
                    /*
                    * On Android versions below 12, setExpedited requires the implementation of
                    * getForegroundInfo and displaying a notification; otherwise, it will crash.
                    * */
                    setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                }
                setConstraints(constraints)
                setInputData(data)
            }
            return worker.build()
        }
    }
}