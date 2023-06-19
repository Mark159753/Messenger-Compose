package com.example.messenger.data.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.messenger.common.UNKNOWN_ERROR
import com.example.messenger.data.network.adapter.NetworkResponse
import com.example.messenger.data.network.models.signIn.dto.SignInDto
import com.example.messenger.data.repository.auth.AuthRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SignInWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val authRepository: AuthRepository
): CoroutineWorker(appContext, workerParams){

    override suspend fun doWork(): Result {
        val dto = SignInDto(
            email = inputData.getString(EMAIL) ?: "",
            password = inputData.getString(PASSWORD) ?: ""
        )

        return when(val res = authRepository.signIn(dto)){
            is NetworkResponse.ApiError -> Result.failure(
                workDataOf(ERROR_MSG to (res.body?.message ?: res.error?.message ?: UNKNOWN_ERROR))
            )
            is NetworkResponse.Success -> Result.success()

        }
    }

    companion object{
        const val ERROR_MSG = "com.example.messenger.data.workers.SignInWorker.ERROR_MSG"

        private const val EMAIL = "com.example.messenger.data.workers.SignInWorker.EMAIL"
        private const val PASSWORD = "com.example.messenger.data.workers.SignInWorker.PASSWORD"

        fun createOneTimeWorkRequest(email:String, password:String): OneTimeWorkRequest {
            val worker = OneTimeWorkRequestBuilder<SignInWorker>()
            val data = workDataOf(
                EMAIL to email,
                PASSWORD to password
            )
            worker.setInputData(data)
            return worker.build()
        }
    }
}