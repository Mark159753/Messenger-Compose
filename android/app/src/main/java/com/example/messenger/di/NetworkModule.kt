package com.example.messenger.di

import com.example.messenger.BuildConfig
import com.example.messenger.data.local.session.SessionManager
import com.example.messenger.data.network.ApiService
import com.example.messenger.data.network.AuthenticationInterceptor
import com.example.messenger.data.network.adapter.NetworkResponseAdapterFactory
import com.example.messenger.data.network.TokenAuthenticator
import com.example.messenger.data.network.socket.SocketManager
import com.example.messenger.data.network.socket.SocketManagerImpl
import com.example.messenger.data.repository.auth.refresh.RefreshAuthTokenRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideAuthenticationInterceptor(sessionManager: SessionManager): AuthenticationInterceptor{
        return AuthenticationInterceptor(sessionManager)
    }

    @Provides
    fun provideTokenAuthenticator(repository: RefreshAuthTokenRepository): TokenAuthenticator {
        return TokenAuthenticator(repository)
    }

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val logger = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG) logger.level = HttpLoggingInterceptor.Level.BODY
        else logger.level = HttpLoggingInterceptor.Level.BASIC
        return logger
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(
        logging: HttpLoggingInterceptor,
        authenticationInterceptor:AuthenticationInterceptor,
        tokenAuthenticator: TokenAuthenticator
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authenticationInterceptor)
            .authenticator(tokenAuthenticator)
            .addInterceptor(logging)
            .build()
    }

    @Singleton
    @Provides
    fun provideApiService(
        client: OkHttpClient
    ): ApiService {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addCallAdapterFactory(NetworkResponseAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(ApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideSocketManager(
        sessionManager:SessionManager,
        scope:CoroutineScope,
        refreshAuthTokenRepository: RefreshAuthTokenRepository
    ):SocketManager = SocketManagerImpl(sessionManager, scope, refreshAuthTokenRepository)

}