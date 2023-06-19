package com.example.messenger.di

import android.content.Context
import com.example.messenger.data.local.proto.user.UserLocalDataSource
import com.example.messenger.data.local.session.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule {

    @Provides
    @Singleton
    fun provideSessionManager(@ApplicationContext context: Context) = SessionManager(context)

    @Provides
    @Singleton
    fun provideUserDataSource(@ApplicationContext context: Context) = UserLocalDataSource(context)
}