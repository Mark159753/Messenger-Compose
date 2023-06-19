package com.example.messenger.data.workers

import android.content.Context
import android.net.Uri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.messenger.common.UNKNOWN_ERROR
import com.example.messenger.common.utils.FileUtils
import com.example.messenger.data.network.adapter.NetworkResponse
import com.example.messenger.data.network.models.signUp.dto.SignUpDto
import com.example.messenger.data.repository.auth.AuthRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.io.File

@HiltWorker
class SendSignUpWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val authRepository: AuthRepository
): CoroutineWorker(appContext, workerParams) {

    private val fileUtils by lazy { FileUtils(applicationContext) }

    override suspend fun doWork(): Result {
        val dto = SignUpDto(
            email = inputData.getString(EMAIL) ?: return Result.failure(),
            password = inputData.getString(PASSWORD) ?: return Result.failure(),
            first_name = inputData.getString(FIRST_NAME) ?: return Result.failure(),
            last_name = inputData.getString(LAST_NAME) ?: return Result.failure(),
            nick_name = inputData.getString(NICK_NAME) ?: return Result.failure(),
            phone = inputData.getString(PHONE) ?: return Result.failure()
        )

        val avatarPath = inputData.getString(AVATAR)
        val avatar = if (!avatarPath.isNullOrBlank())
            File(fileUtils.getPath(Uri.parse(avatarPath)))
        else null

        return when( val res = authRepository.signUp(dto, avatar)){
            is NetworkResponse.ApiError -> Result
                .failure(
                    workDataOf(
                        SIGN_UP_ERROR to (res.body?.message ?: UNKNOWN_ERROR))
                )
            is NetworkResponse.Success -> Result.success()
        }
    }

    companion object{

        const val SIGN_UP_ERROR = "com.example.messenger.data.workers.SendSignUpWorker.SIGN_UP_ERROR"

        private const val FIRST_NAME = "com.example.messenger.data.workers.SendSignUpWorker.FIRST_NAME"
        private const val LAST_NAME = "com.example.messenger.data.workers.SendSignUpWorker.LAST_NAME"
        private const val NICK_NAME = "com.example.messenger.data.workers.SendSignUpWorker.NICK_NAME"
        private const val EMAIL = "com.example.messenger.data.workers.SendSignUpWorker.EMAIL"
        private const val PHONE = "com.example.messenger.data.workers.SendSignUpWorker.PHONE"
        private const val PASSWORD = "com.example.messenger.data.workers.SendSignUpWorker.PASSWORD"
        private const val AVATAR = "com.example.messenger.data.workers.SendSignUpWorker.AVATAR"

        fun createOneTimeWorkRequest(dto: SignUpDto, avatar: Uri? = null): OneTimeWorkRequest {
            val worker = OneTimeWorkRequestBuilder<SendSignUpWorker>()
            val data = workDataOf(
                FIRST_NAME to  dto.first_name,
                LAST_NAME to dto.last_name,
                NICK_NAME to dto.nick_name,
                EMAIL to dto.email,
                PHONE to dto.phone,
                PASSWORD to dto.password,
                AVATAR to avatar?.toString()
            )
            worker.setInputData(data)
            return worker.build()
        }
    }
}