package com.example.messenger.data.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.messenger.common.UNKNOWN_ERROR
import com.example.messenger.data.local.proto.user.UserLocalDataSource
import com.example.messenger.data.network.adapter.NetworkResponse
import com.example.messenger.data.repository.user.UserRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class FetchUserWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val userRepository: UserRepository,
    private val localUserLocalDataSource: UserLocalDataSource
): CoroutineWorker(appContext, workerParams){

    override suspend fun doWork(): Result {
        return when(val res =  userRepository.fetchMyUser()){
            is NetworkResponse.ApiError -> Result.failure(workDataOf(ERROR_MSG to (res.body?.message ?: res.error?.message ?: UNKNOWN_ERROR)))
            is NetworkResponse.Success -> {
                localUserLocalDataSource.updateUser(res.body)
                Result.success()
            }
        }
    }

    companion object{
        const val ERROR_MSG = "com.example.messenger.data.workers.FetchUserWorker"

        fun createOneTimeWorkRequest(): OneTimeWorkRequest {
            val worker = OneTimeWorkRequestBuilder<FetchUserWorker>()
            return worker.build()
        }
    }
}