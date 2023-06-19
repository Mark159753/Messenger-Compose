package com.example.messenger.data.repository.images

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.messenger.common.PAGING_SIZE
import com.example.messenger.data.local.provider.images.Image
import com.example.messenger.data.local.provider.images.LocalImagesDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalImagesRepositoryImpl @Inject constructor(
    private val imagesDataSource: LocalImagesDataSource
):LocalImagesRepository {

    override fun getImages(): Flow<PagingData<Image>> {
        return Pager(
            config = PagingConfig(pageSize = PAGING_SIZE, enablePlaceholders = false),
            pagingSourceFactory = { ImagesPagingSource(imagesDataSource) }
        ).flow
    }
}