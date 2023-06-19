package com.example.messenger.data.network.models.message

data class ImageItem(
    val created_at: String,
    val id: String,
    val mineType: String,
    val name: String,
    val originalName: String,
    val path: String,
    val size: Int,
    val updated_at: String
)