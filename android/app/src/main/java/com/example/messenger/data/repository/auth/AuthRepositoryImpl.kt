package com.example.messenger.data.repository.auth

import com.example.messenger.common.exstantions.toMultiPartBody
import com.example.messenger.common.exstantions.toPartMap
import com.example.messenger.data.local.proto.user.UserLocalDataSource
import com.example.messenger.data.local.session.SessionManager
import com.example.messenger.data.network.ApiService
import com.example.messenger.data.network.adapter.NetworkResponse
import com.example.messenger.data.network.models.signIn.dto.SignInDto
import com.example.messenger.data.network.models.signUp.dto.SignUpDto
import com.example.messenger.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val service: ApiService,
    private val sessionManager: SessionManager,
    private val userLocalDataSource: UserLocalDataSource,
    @IoDispatcher
    private val dispatcher: CoroutineDispatcher
):AuthRepository {

    override suspend fun signIn(dto: SignInDto) = withContext(dispatcher){
        return@withContext  when(val res = service.signIn(dto)){
            is NetworkResponse.ApiError -> res
            is NetworkResponse.Success -> {
                sessionManager.saveAuthToken(res.body.accessToken)
                sessionManager.saveRefreshToken(res.body.refreshToken)
                res
            }
        }
    }

    override suspend fun signUp(dto: SignUpDto, avatar: File?) = withContext(dispatcher){
        return@withContext when(val res = service.signUp(dto.toPartMap(), avatar?.toMultiPartBody("avatar", "image/*"))){
            is NetworkResponse.ApiError -> res
            is NetworkResponse.Success -> {
                sessionManager.saveAuthToken(res.body.accessToken)
                sessionManager.saveRefreshToken(res.body.refreshToken)
                res
            }
        }
    }

    override suspend fun logout() = withContext(dispatcher){
        return@withContext when(val res = service.logout()){
            is NetworkResponse.ApiError -> res
            is NetworkResponse.Success -> {
                userLocalDataSource.clear()
                sessionManager.clear()
                res
            }
        }
    }
}