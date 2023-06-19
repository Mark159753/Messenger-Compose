package com.example.messenger.data.network.models.message

import com.example.messenger.data.network.models.user.UserResponse

data class MessageResponse(
    val author: UserResponse,
    val chatId: String,
    val created_at: String,
    val id: String,
    val message: String?,
    val updated_at: String,
    val images:List<ImageItem>
)