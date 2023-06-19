package com.example.messenger.di

import android.content.Context
import androidx.room.Room
import com.example.messenger.data.local.db.MessengerDb
import com.example.messenger.data.local.db.dao.SearchDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MessengerDb {
        return Room.databaseBuilder(context, MessengerDb::class.java, "messenger-db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideSearchDao(db:MessengerDb): SearchDao = db.getSearchDao()

}