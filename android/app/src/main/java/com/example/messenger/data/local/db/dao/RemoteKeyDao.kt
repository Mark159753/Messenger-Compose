package com.example.messenger.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.messenger.data.local.db.entities.RemoteKeyEntity

@Dao
interface RemoteKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: RemoteKeyEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllItems(items:List<RemoteKeyEntity>)

    @Query("SELECT * FROM remote_keys")
    suspend fun getAllItems():List<RemoteKeyEntity>

    @Query("SELECT * FROM remote_keys WHERE type = :type")
    suspend fun getByType(type:Int):List<RemoteKeyEntity>

    @Query("SELECT * FROM remote_keys WHERE id = :id")
    suspend fun getItemById(id:String): RemoteKeyEntity?

    @Query("DELETE FROM remote_keys WHERE type = :type")
    suspend fun deleteAllByType(type:Int)

    @Query("DELETE FROM remote_keys")
    suspend fun deleteAll()
}