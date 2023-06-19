package com.example.messenger.data.network.models.user

data class UserResponse(
    val avatar: Avatar?,
    val created_at: String,
    val email: String,
    val first_name: String,
    val id: String,
    val last_name: String,
    val nick_name: String,
    val phone: String,
    val updated_at: String
)