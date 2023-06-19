package com.example.messenger.data.network.models.message.dto

import com.example.messenger.data.local.db.entities.MessageEntity

data class MessageDto(
    val message:String?,
    val chatId:String,
    val id:String? = null
)

fun MessageEntity.toDto() = MessageDto(
    id = id,
    message = message,
    chatId = chatId
)
