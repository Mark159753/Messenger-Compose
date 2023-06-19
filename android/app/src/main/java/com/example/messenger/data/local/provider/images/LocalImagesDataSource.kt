package com.example.messenger.data.local.provider.images

interface LocalImagesDataSource {

    suspend fun getImagesByPage(page:Int, size:Int): MutableList<Image>
}