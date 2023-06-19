package com.example.messenger.data.repository.search

import androidx.paging.PagingData
import com.example.messenger.data.local.db.entities.SearchEntity
import kotlinx.coroutines.flow.Flow

interface SearchRepository {

    fun getSearch(query: String): Flow<PagingData<SearchEntity>>
}