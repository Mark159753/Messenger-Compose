package com.example.messenger.data.repository.images

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.messenger.data.local.provider.images.Image
import com.example.messenger.data.local.provider.images.LocalImagesDataSource

class ImagesPagingSource(
    private val dataSource: LocalImagesDataSource
):PagingSource<Int, Image>() {

    override fun getRefreshKey(state: PagingState<Int, Image>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Image> {
        return try {
            val key = params.key ?: 1
            val list = dataSource.getImagesByPage(page = key, size = params.loadSize)
            LoadResult.Page(
                data = list,
                prevKey = if (key == 1) null else key - 1,
                nextKey = if (list.isEmpty()) null else key + 1
            )
        }catch (e:Exception){
            LoadResult.Error(e)
        }
    }
}