package com.example.messenger.data.repository.user

import com.example.messenger.common.GenericResponse
import com.example.messenger.data.network.models.user.UserResponse

interface UserRepository {

    suspend fun fetchMyUser(): GenericResponse<UserResponse>
}