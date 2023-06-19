package com.example.messenger.data.repository.auth.refresh

import android.util.Log
import com.example.messenger.BuildConfig
import com.example.messenger.common.AUTH_TOKEN_TYPE
import com.example.messenger.data.local.session.SessionManager
import com.example.messenger.data.network.models.taken.TokenResponse
import com.example.messenger.di.IoDispatcher
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject

class RefreshAuthTokenRepositoryImpl @Inject constructor(
    private val sessionManager: SessionManager,
    @IoDispatcher
    private val dispatcher: CoroutineDispatcher
): RefreshAuthTokenRepository {

    override suspend fun refreshToken() = withContext(dispatcher){
        val token = sessionManager.refreshToken.firstOrNull() ?: return@withContext null
        val response = getNewToken(token)

        sessionManager.saveRefreshToken(response?.refreshToken)
        sessionManager.saveAuthToken(response?.accessToken)

        response?.accessToken
    }

    private fun getNewToken(refreshToken:String): TokenResponse?{
        val url = BuildConfig.BASE_URL + "auth/refresh"
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "$AUTH_TOKEN_TYPE $refreshToken")
            .build()

        Log.i("RefreshAuthTokenRepositoryImpl", url)

        return try {
            val res = client.newCall(request).execute()
            val gson = Gson()
            gson.fromJson(res.body?.string(), TokenResponse::class.java)
        }catch (e:Exception){
            Log.e("ERROR", e.stackTraceToString())
            null
        }
    }
}