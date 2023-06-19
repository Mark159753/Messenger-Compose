package com.example.messenger.data.local.provider.images

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Image(
    val id:Long,
    val name:String,
    val size:Int,
    val path: String,
    val created:String,
    val uri: Uri
):Parcelable
