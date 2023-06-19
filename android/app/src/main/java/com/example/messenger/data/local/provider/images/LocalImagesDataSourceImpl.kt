package com.example.messenger.data.local.provider.images

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import com.example.messenger.common.utils.FileUtils
import com.example.messenger.di.DefaultDispatcher
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LocalImagesDataSourceImpl @Inject constructor(
    @ApplicationContext
    private val context: Context,
    @DefaultDispatcher
    private val dispatcher: CoroutineDispatcher,
    private val fileUtils: FileUtils
): LocalImagesDataSource {

    private val collection =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(
                MediaStore.VOLUME_EXTERNAL
            )
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

    private val projection = arrayOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.DISPLAY_NAME,
        MediaStore.Images.Media.DATE_ADDED,
        MediaStore.Images.Media.SIZE
    )

    private val sortOrder = MediaStore.Images.Media.DATE_ADDED

    override suspend fun getImagesByPage(page:Int, size:Int): MutableList<Image> {
        val skip = (page - 1) * size
        return getImages(size, skip)
    }

    private suspend fun getImages(limit: Int, offset: Int) = withContext(dispatcher){
        val images = mutableListOf<Image>()

        val query = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val bundle = Bundle().apply {
                // sort
                putString(
                    ContentResolver.QUERY_ARG_SORT_COLUMNS,
                    sortOrder
                )
                putInt(
                    ContentResolver.QUERY_ARG_SORT_DIRECTION,
                    ContentResolver.QUERY_SORT_DIRECTION_DESCENDING
                )
                // limit, offset
                putInt(ContentResolver.QUERY_ARG_LIMIT, limit)
                putInt(ContentResolver.QUERY_ARG_OFFSET, offset)
            }
            context.contentResolver.query(collection, projection, bundle, null)
        }else{
            context.contentResolver.query(
                collection,
                projection,
                null,
                null,
                "$sortOrder DESC LIMIT $limit OFFSET $offset"
            )
        }

        query?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val createdColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)

            while (cursor.moveToNext()){
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val created = cursor.getString(createdColumn)
                val size = cursor.getInt(sizeColumn)

                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                images.add(
                    Image(
                        id = id,
                        name = name,
                        size = size,
                        path = fileUtils.getPath(contentUri),
                        created = created,
                        uri = contentUri.normalizeScheme()
                    )
                )
            }
        }
        images
    }
}