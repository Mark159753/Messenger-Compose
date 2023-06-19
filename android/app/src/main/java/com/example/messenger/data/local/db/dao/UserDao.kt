package com.example.messenger.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.messenger.data.local.db.entities.UserEntity

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllItems(items:List<UserEntity>)

    @Query("SELECT * FROM users")
    suspend fun getAllItems():List<UserEntity>

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getItemById(id:String): UserEntity?

    @Query("DELETE FROM users")
    suspend fun deleteAllItems()

    @Query("DELETE FROM users WHERE id = :id")
    suspend fun deleteById(id: String)
}