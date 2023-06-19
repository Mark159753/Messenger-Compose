package com.example.messenger.data.local.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.messenger.data.network.models.chat.list.ChatResponseItem

@Entity("chats")
data class ChatEntity(
    @PrimaryKey
    val id:String,
    val messageId:String?,
    val userId:String
)

fun ChatResponseItem.toEntity() = ChatEntity(
    id = id,
    messageId = messages.firstOrNull()?.id,
    userId = users.first().id
)
