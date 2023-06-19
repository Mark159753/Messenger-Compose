package com.example.messenger.data.network.models.chat.list

import com.example.messenger.data.network.models.message.ImageItem
import com.example.messenger.data.network.models.user.UserResponse

data class Message(
    val chatId: String,
    val created_at: String,
    val id: String,
    val message: String?,
    val updated_at: String,
    val author:UserResponse,
    val images:List<ImageItem>
)