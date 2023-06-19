package com.example.messenger.data.network

import com.example.messenger.common.AUTH_TOKEN_TYPE
import com.example.messenger.data.local.session.SessionManager
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthenticationInterceptor @Inject constructor(
    private val sessionManager: SessionManager
):Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking {
            sessionManager.authToken.firstOrNull()
        }
        val newUrlBuilder = chain.request()
            .newBuilder()
        if (!token.isNullOrBlank()){
            newUrlBuilder.addHeader("Authorization", "$AUTH_TOKEN_TYPE $token")
        }
        return chain.proceed(newUrlBuilder.build())
    }
}