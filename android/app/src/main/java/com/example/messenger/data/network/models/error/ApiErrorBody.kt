package com.example.messenger.data.network.models.error

data class ApiErrorBody(
    val statusCode:Int,
    val timestamp:String,
    val message:String?
)
