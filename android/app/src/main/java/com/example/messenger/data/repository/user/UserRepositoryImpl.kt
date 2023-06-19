package com.example.messenger.data.repository.user

import com.example.messenger.data.local.proto.user.UserLocalDataSource
import com.example.messenger.data.network.ApiService
import com.example.messenger.data.network.adapter.NetworkResponse
import com.example.messenger.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val userLocalDataSource: UserLocalDataSource,
    @IoDispatcher
    private val dispatcher: CoroutineDispatcher
): UserRepository {

    override suspend fun fetchMyUser() = withContext(dispatcher){
        when(val res = apiService.getMyProfile()){
            is NetworkResponse.ApiError -> return@withContext res
            is NetworkResponse.Success -> {
                userLocalDataSource.updateUser(res.body)
                return@withContext res
            }
        }
    }

}