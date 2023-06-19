package com.example.messenger.ui.search.model

import com.example.messenger.data.local.db.entities.SearchEntity
import com.example.messenger.domain.date.relativeFormatDate

data class ItemSearchModel(
    val id:String,
    val avatar:String?,
    val name:String,
    val isOnline:Boolean,
    val message:ItemMessage?
)

data class ItemMessage(
    val id:String?,
    val chatId:String?,
    val message:String?,
    val created:String?
)

fun SearchEntity.toUiModel() = ItemSearchModel(
    id = id,
    avatar = avatar,
    name = "$first_name $last_name",
    isOnline = isOnline,
    message = if (!chatMessageId.isNullOrBlank())
        ItemMessage(
            id = chatMessageId,
            chatId = chatId,
            message = chatMessage,
            created = chatMessageCreated?.relativeFormatDate()
        )
            else null
)
