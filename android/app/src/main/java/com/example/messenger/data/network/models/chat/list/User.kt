package com.example.messenger.data.network.models.chat.list

import com.example.messenger.data.network.models.user.Avatar

data class User(
    val created_at: String,
    val email: String,
    val first_name: String,
    val id: String,
    val isOnline: Boolean,
    val last_name: String,
    val nick_name: String,
    val phone: String,
    val updated_at: String,
    val avatar: Avatar?
)