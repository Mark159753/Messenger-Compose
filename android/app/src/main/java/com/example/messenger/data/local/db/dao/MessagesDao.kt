package com.example.messenger.data.local.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.messenger.data.local.db.entities.MessageEntity
import com.example.messenger.data.local.db.entities.relation.MessageWithAuthor

@Dao
interface MessagesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: MessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllItems(items:List<MessageEntity>)

    @Transaction
    @Query("SELECT * FROM messages_tab WHERE messages_tab.chatId = :chatId ORDER BY messages_tab.created_at DESC")
    fun getMessagesByChat(chatId:String): PagingSource<Int, MessageWithAuthor>

    @Query("SELECT * FROM messages_tab")
    suspend fun getAllItems():List<MessageEntity>

    @Query("SELECT * FROM messages_tab WHERE id = :id")
    suspend fun getItemById(id:String): MessageEntity?

    @Query("DELETE FROM messages_tab")
    suspend fun deleteAllItems()

    @Query("DELETE FROM messages_tab WHERE chatId = :chatId")
    suspend fun deleteAllItemsByChat(chatId:String)

    @Query("DELETE FROM messages_tab WHERE id = :id")
    suspend fun deleteById(id: String)
}