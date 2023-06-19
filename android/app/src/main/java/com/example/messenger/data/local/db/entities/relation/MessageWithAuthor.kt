package com.example.messenger.data.local.db.entities.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.example.messenger.data.local.db.entities.MessageEntity
import com.example.messenger.data.local.db.entities.UserEntity
import com.example.messenger.data.local.db.entities.toEntity
import com.example.messenger.data.network.models.message.MessageResponse
import com.example.messenger.data.network.models.message.dto.MessageDto

data class MessageWithAuthor(
    @Embedded
    val message:MessageEntity,
    @Relation(
        parentColumn = "authorId",
        entityColumn = "id"
    )
    val author: UserEntity
)

fun MessageWithAuthor.toDto() = MessageDto(
    id = message.id,
    chatId = message.chatId,
    message = message.message,
)

fun MessageResponse.toEntityWithAuthor() = MessageWithAuthor(
    message = toEntity(),
    author = author.toEntity()
)