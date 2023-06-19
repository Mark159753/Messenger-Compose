package com.example.messenger.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.messenger.data.local.db.converter.ArrayListConverter
import com.example.messenger.data.local.db.dao.ChatDao
import com.example.messenger.data.local.db.dao.MessagesDao
import com.example.messenger.data.local.db.dao.RemoteKeyDao
import com.example.messenger.data.local.db.dao.SearchDao
import com.example.messenger.data.local.db.dao.UserDao
import com.example.messenger.data.local.db.entities.ChatEntity
import com.example.messenger.data.local.db.entities.MessageEntity
import com.example.messenger.data.local.db.entities.RemoteKeyEntity
import com.example.messenger.data.local.db.entities.SearchEntity
import com.example.messenger.data.local.db.entities.UserEntity

@Database(
    entities = [
        SearchEntity::class,
        MessageEntity::class,
        RemoteKeyEntity::class,
        UserEntity::class,
        ChatEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(ArrayListConverter::class)
abstract class MessengerDb: RoomDatabase() {

    abstract fun getSearchDao():SearchDao
    abstract fun getMessagesDao(): MessagesDao
    abstract fun getRemoteKeysDao():RemoteKeyDao
    abstract fun getUserDao():UserDao
    abstract fun getChatDao():ChatDao
}