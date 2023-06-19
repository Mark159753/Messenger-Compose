package com.example.messenger.data.local.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.messenger.data.network.models.search.SearchResponseItem

@Entity(tableName = "search_entity")
data class SearchEntity(
    @PrimaryKey
    val id:String,
    val created_at: String,
    val email: String,
    val avatar:String?,
    val first_name: String,
    val isOnline: Boolean,
    val last_name: String,
    val nick_name: String,
    val phone: String,
    val updated_at: String,
    val chatId:String?,
    val chatMessage:String?,
    val chatMessageId:String?,
    val chatMessageCreated:String?
)

fun SearchResponseItem.toEntity() = SearchEntity(
    id = id,
    created_at = created_at,
    email = email,
    avatar = avatar?.path,
    first_name = first_name,
    last_name = last_name,
    isOnline = isOnline,
    nick_name = nick_name,
    phone = phone,
    updated_at = updated_at,
    chatId = chats.firstOrNull()?.id,
    chatMessage = chats.firstOrNull()?.messages?.firstOrNull()?.message,
    chatMessageId = chats.firstOrNull()?.messages?.firstOrNull()?.id,
    chatMessageCreated = chats.firstOrNull()?.messages?.firstOrNull()?.created_at,
)
