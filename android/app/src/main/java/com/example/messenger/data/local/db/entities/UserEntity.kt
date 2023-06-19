package com.example.messenger.data.local.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.messenger.data.network.models.chat.list.User
import com.example.messenger.data.network.models.user.UserResponse

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id:String,
    val created_at: String,
    val email: String,
    val first_name: String,
    val last_name: String,
    val nick_name: String,
    val phone: String,
    val updated_at: String,
    val avatar:String?,
    val isOnline:Boolean
)

fun User.toEntity() = UserEntity(
    id = id,
    created_at = created_at,
    email = email,
    first_name = first_name,
    last_name = last_name,
    nick_name = nick_name,
    phone = phone,
    updated_at = updated_at,
    avatar = avatar?.path,
    isOnline = isOnline
)

fun UserResponse.toEntity() = UserEntity(
    id = id,
    created_at = created_at,
    email = email,
    first_name = first_name,
    last_name = last_name,
    nick_name = nick_name,
    phone = phone,
    updated_at = updated_at,
    avatar = avatar?.path,
    isOnline = false
)
