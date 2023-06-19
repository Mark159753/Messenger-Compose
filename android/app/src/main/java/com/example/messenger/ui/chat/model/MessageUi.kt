package com.example.messenger.ui.chat.model

import com.example.messenger.data.local.db.entities.relation.MessageWithAuthor

sealed interface MessageItemUi{
    data class MessageUiItem(
        val user:UserUi,
        val message:MessageUi,
        val isCompanion:Boolean
    ): MessageItemUi

    data class StickyHeader(val title:String, val id: String):MessageItemUi
}

data class MessageUi(
    val id:String,
    val created_at:String,
    val message:String?,
    val images:List<String>?
)

data class UserUi(
    val id:String,
    val created_at:String,
    val email:String,
    val name:String,
    val nick_name:String,
    val phone:String,
    val avatar:String?
)

fun MessageWithAuthor.toUi(isCompanion: Boolean):MessageItemUi = MessageItemUi.MessageUiItem(
    user = UserUi(
        id = author.id,
        created_at = author.created_at,
        email = author.email,
        name = "${author.first_name} ${author.last_name}",
        nick_name = author.nick_name,
        phone = author.phone,
        avatar = author.avatar
    ),
    message = MessageUi(
        id = message.id,
        created_at = message.created_at,
        message = message.message,
        images = message.images
    ),
    isCompanion = isCompanion
)
