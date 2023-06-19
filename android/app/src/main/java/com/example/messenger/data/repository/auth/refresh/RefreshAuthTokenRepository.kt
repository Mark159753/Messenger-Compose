package com.example.messenger.data.repository.auth.refresh

interface RefreshAuthTokenRepository {

    suspend fun refreshToken():String?
}