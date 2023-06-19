package com.example.messenger.ui.chat.model

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.navigation.NavType
import com.google.gson.Gson
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChatArgs(
    val userId:String,
    val name:String,
    val avatar:String?,
    val isOnline:Boolean,
):Parcelable{
    fun toUri() = Uri.encode(Gson().toJson(this))
}