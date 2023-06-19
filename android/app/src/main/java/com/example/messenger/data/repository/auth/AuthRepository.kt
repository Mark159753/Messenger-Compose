package com.example.messenger.data.repository.auth

import com.example.messenger.common.GenericResponse
import com.example.messenger.data.network.models.signIn.dto.SignInDto
import com.example.messenger.data.network.models.signUp.dto.SignUpDto
import com.example.messenger.data.network.models.taken.TokenResponse
import java.io.File

interface AuthRepository {

    suspend fun signIn(dto: SignInDto):GenericResponse<TokenResponse>

    suspend fun signUp(dto: SignUpDto, avatar: File?):GenericResponse<TokenResponse>

    suspend fun logout():GenericResponse<Unit>
}