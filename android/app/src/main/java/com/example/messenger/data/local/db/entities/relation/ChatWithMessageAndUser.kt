package com.example.messenger.data.local.db.entities.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.example.messenger.data.local.db.entities.ChatEntity
import com.example.messenger.data.local.db.entities.MessageEntity
import com.example.messenger.data.local.db.entities.UserEntity

data class ChatWithMessageAndUser(
    @Embedded val chat:ChatEntity,
    @Relation(
        parentColumn = "messageId",
        entityColumn = "id"
    )
    val message:MessageEntity?,
    @Relation(
        parentColumn = "userId",
        entityColumn = "id"
    )
    val user:UserEntity
)
