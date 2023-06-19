package com.example.messenger.di

import android.content.Context
import com.example.messenger.common.utils.FileUtils
import com.example.messenger.data.local.db.MessengerDb
import com.example.messenger.data.local.proto.user.UserLocalDataSource
import com.example.messenger.domain.chat.OnRemoveChat
import com.example.messenger.domain.message.GenerateMessage
import com.example.messenger.domain.message.OnNewMessage
import com.example.messenger.domain.message.SendMessage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideGenerateMessage(
        userLocalDataSource: UserLocalDataSource
    ): GenerateMessage = GenerateMessage(userLocalDataSource)

    @Provides
    fun provideFileUtils(
        @ApplicationContext
        context: Context
    ): FileUtils = FileUtils(context)


    @Provides
    fun provideOnNewMessage(
        db: MessengerDb,
        userLocalDataSource: UserLocalDataSource,
        @IoDispatcher
        dispatcher: CoroutineDispatcher
    ): OnNewMessage = OnNewMessage(db, userLocalDataSource, dispatcher)

    @Provides
    fun provideOnRemoveChat(
        db: MessengerDb,
        @IoDispatcher
        dispatcher: CoroutineDispatcher
    ): OnRemoveChat = OnRemoveChat(db, dispatcher)


    @Provides
    fun provideSendMessage(
        @ApplicationContext
        context:Context,
        db: MessengerDb,
        generateMessage: GenerateMessage,
        @IoDispatcher
        dispatcher: CoroutineDispatcher
    ): SendMessage = SendMessage(context, db, generateMessage, dispatcher)
}