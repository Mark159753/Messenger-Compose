package com.example.messenger.data.network.models.search

import com.example.messenger.data.network.models.message.MessageResponse

data class Chat(
    val created_at: String,
    val id: String,
    val messages: List<MessageResponse>,
    val updated_at: String
)