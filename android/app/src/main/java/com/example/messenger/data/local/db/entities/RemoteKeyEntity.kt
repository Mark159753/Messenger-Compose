package com.example.messenger.data.local.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKeyEntity(
    @PrimaryKey
    val id:String,
    val prevKey:Int?,
    val nextKey:Int?,
    val type:Int
)

const val REMOTE_KEY_MESSAGES = 1
const val REMOTE_KEY_SEARCH = 2
const val REMOTE_KEY_CHAT = 2
