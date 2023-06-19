package com.example.messenger.data.network.socket

import com.example.messenger.data.network.models.message.MessageResponse
import com.example.messenger.data.network.models.socket.chat.RemoveChatMsg
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface SocketManager {

    val connectionState:StateFlow<ConnectionState>
    val messages:SharedFlow<MessageResponse>
    val onRemoveChat:SharedFlow<RemoveChatMsg>

    suspend fun connect()

    fun disconnect()
}