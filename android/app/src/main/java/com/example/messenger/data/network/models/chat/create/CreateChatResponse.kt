package com.example.messenger.data.network.models.chat.create

data class CreateChatResponse(
    val created_at: String,
    val id: String,
    val updated_at: String,
    val users: List<User>
)