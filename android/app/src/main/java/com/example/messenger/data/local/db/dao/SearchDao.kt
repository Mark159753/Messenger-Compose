package com.example.messenger.data.local.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.messenger.data.local.db.entities.SearchEntity

@Dao
interface SearchDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item:SearchEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllItems(items:List<SearchEntity>)

    @Query("SELECT * FROM search_entity")
    fun getPagingSource(): PagingSource<Int, SearchEntity>


    @Query("SELECT * FROM search_entity WHERE" +
            " nick_name LIKE :queryString OR email LIKE :queryString " +
            "OR phone LIKE :queryString OR first_name LIKE :queryString OR last_name LIKE :queryString")
    fun getSearchPaging(queryString: String): PagingSource<Int, SearchEntity>

    @Query("SELECT * FROM search_entity")
    suspend fun getAllItems():List<SearchEntity>

    @Query("SELECT * FROM search_entity WHERE id = :id")
    suspend fun getItemById(id:String):SearchEntity?

    @Query("DELETE FROM search_entity")
    suspend fun deleteAllItems()

    @Query("DELETE FROM search_entity WHERE id = :id")
    suspend fun deleteById(id:String)
}