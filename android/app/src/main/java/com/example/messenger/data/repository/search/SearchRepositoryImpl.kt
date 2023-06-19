package com.example.messenger.data.repository.search

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.messenger.common.PAGING_SIZE
import com.example.messenger.data.local.db.MessengerDb
import com.example.messenger.data.local.db.entities.SearchEntity
import com.example.messenger.data.network.ApiService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val apiService:ApiService,
    private val db:MessengerDb
):SearchRepository {

    override fun getSearch(query: String): Flow<PagingData<SearchEntity>> {
        Log.d("SearchRepositoryImpl", "New query: $query")

        val dbQuery = "%${query.replace(' ', '%')}%"
        val pagingSourceFactory = { db.getSearchDao().getSearchPaging(dbQuery)}

        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(pageSize = PAGING_SIZE, enablePlaceholders = false),
            remoteMediator = SearchRemoteMediator(
                query,
                db,
                apiService
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

}