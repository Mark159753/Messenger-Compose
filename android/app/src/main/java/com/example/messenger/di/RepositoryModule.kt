package com.example.messenger.di

import com.example.messenger.data.local.provider.images.LocalImagesDataSource
import com.example.messenger.data.local.provider.images.LocalImagesDataSourceImpl
import com.example.messenger.data.repository.auth.AuthRepository
import com.example.messenger.data.repository.auth.AuthRepositoryImpl
import com.example.messenger.data.repository.auth.refresh.RefreshAuthTokenRepository
import com.example.messenger.data.repository.auth.refresh.RefreshAuthTokenRepositoryImpl
import com.example.messenger.data.repository.chat.ChatRepository
import com.example.messenger.data.repository.chat.ChatRepositoryImpl
import com.example.messenger.data.repository.images.LocalImagesRepository
import com.example.messenger.data.repository.images.LocalImagesRepositoryImpl
import com.example.messenger.data.repository.messages.MessagesRepository
import com.example.messenger.data.repository.messages.MessagesRepositoryImpl
import com.example.messenger.data.repository.search.SearchRepository
import com.example.messenger.data.repository.search.SearchRepositoryImpl
import com.example.messenger.data.repository.user.UserRepository
import com.example.messenger.data.repository.user.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindUserRepository(
        repositoryImpl: UserRepositoryImpl
    ):UserRepository


    @Binds
    abstract fun bindRefreshAuthTokenRepository(
        repositoryImpl: RefreshAuthTokenRepositoryImpl
    ): RefreshAuthTokenRepository

    @Binds
    abstract fun bindAuthRepository(
        repositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    abstract fun bindSearchRepository(
        repositoryImpl: SearchRepositoryImpl
    ): SearchRepository

    @Binds
    abstract fun bindMessagesRepository(
        repositoryImpl: MessagesRepositoryImpl
    ): MessagesRepository

    @Binds
    abstract fun bindChatRepository(
        repositoryImpl: ChatRepositoryImpl
    ): ChatRepository

    @Binds
    abstract fun bindLocalImagesDataSource(
        repositoryImpl: LocalImagesDataSourceImpl
    ): LocalImagesDataSource

    @Binds
    abstract fun bindLocalImagesRepository(
        repositoryImpl: LocalImagesRepositoryImpl
    ): LocalImagesRepository


}