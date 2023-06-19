package com.example.messenger.data.network.models.chat.list

data class ChatResponseItem(
    val created_at: String,
    val id: String,
    val messages: List<Message>,
    val updated_at: String,
    val users: List<User>
)