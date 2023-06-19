package com.example.messenger.data.network

import com.example.messenger.common.AUTH_TOKEN_TYPE
import com.example.messenger.data.repository.auth.refresh.RefreshAuthTokenRepository
import kotlinx.coroutines.runBlocking
import okhttp3.*
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val refreshTokenRepository: RefreshAuthTokenRepository
): Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        return refreshToken()?.takeIf { it.isNotBlank() }?.let { token ->
            response.request.newBuilder()
                .header("Authorization", "$AUTH_TOKEN_TYPE $token")
                .build()
        }
    }

    private fun refreshToken():String? = runBlocking {
        refreshTokenRepository.refreshToken()
    }

}