package com.example.messenger.data.local.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.messenger.data.local.db.entities.ChatEntity
import com.example.messenger.data.local.db.entities.relation.ChatWithMessageAndUser

@Dao
interface ChatDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ChatEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllItems(items:List<ChatEntity>)

    @Query("SELECT * FROM chats")
    suspend fun getAllItems():List<ChatEntity>

    @Query("SELECT * FROM chats WHERE id = :id")
    suspend fun getItemById(id:String): ChatEntity?

    @Transaction
    @Query("SELECT * FROM chats " +
            "LEFT JOIN messages_tab ON chats.messageId = messages_tab.id " +
            "LEFT JOIN users ON chats.userId = users.id " +
            "ORDER BY messages_tab.created_at DESC")
    fun getAllPaging(): PagingSource<Int, ChatWithMessageAndUser>

    @Query("DELETE FROM chats")
    suspend fun deleteAllItems()

    @Query("DELETE FROM chats WHERE id = :id")
    suspend fun deleteById(id: String)
}