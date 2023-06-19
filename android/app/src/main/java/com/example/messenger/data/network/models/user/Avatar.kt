package com.example.messenger.data.network.models.user

data class Avatar(
    val id: String,
    val mineType: String,
    val name: String,
    val originalName: String,
    val path: String,
    val size: Int
)