package com.example.messenger.data.local.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.messenger.BuildConfig
import com.example.messenger.data.network.models.chat.list.Message
import com.example.messenger.data.network.models.message.MessageResponse

@Entity(tableName = "messages_tab")
data class MessageEntity(
    @PrimaryKey
    val id:String,
    val chatId: String,
    val created_at: String,
    val message: String?,
    val updated_at: String,
    val authorId:String,
    val images:List<String> = emptyList()
)

fun MessageResponse.toEntity() = MessageEntity(
    id = id,
    chatId = chatId,
    created_at = created_at,
    message = message,
    updated_at = updated_at,
    authorId = author.id,
    images = images.map { BuildConfig.BASE_URL + it.path }
)

fun Message.toEntity() = MessageEntity(
    id = id,
    chatId = chatId,
    created_at = created_at,
    message = message,
    updated_at = updated_at,
    authorId = author.id,
    images = images.map { BuildConfig.BASE_URL + it.path }
)

