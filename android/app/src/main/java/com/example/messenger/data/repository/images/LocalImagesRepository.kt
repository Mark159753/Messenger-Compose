package com.example.messenger.data.repository.images

import androidx.paging.PagingData
import com.example.messenger.data.local.provider.images.Image
import kotlinx.coroutines.flow.Flow

interface LocalImagesRepository {

    fun getImages(): Flow<PagingData<Image>>
}