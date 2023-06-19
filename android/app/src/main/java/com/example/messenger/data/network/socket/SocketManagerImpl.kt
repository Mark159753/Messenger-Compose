package com.example.messenger.data.network.socket

import android.util.Log
import com.example.messenger.BuildConfig
import com.example.messenger.common.AUTH_TOKEN_TYPE
import com.example.messenger.common.SOCKET_RECONNECT_DELAY
import com.example.messenger.common.exstantions.toModelOrNull
import com.example.messenger.data.local.session.SessionManager
import com.example.messenger.data.network.models.message.MessageResponse
import com.example.messenger.data.network.models.socket.chat.RemoveChatMsg
import com.example.messenger.data.network.models.socket.error.SocketErrorMsg
import com.example.messenger.data.repository.auth.refresh.RefreshAuthTokenRepository
import com.google.gson.Gson
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

class SocketManagerImpl @Inject constructor(
    private val sessionManager: SessionManager,
    private val scope: CoroutineScope,
    private val refreshTokenRepository: RefreshAuthTokenRepository
): SocketManager {

    private val gson = Gson()

    private var socket:Socket? = null

    private val _connectionState = MutableStateFlow(ConnectionState.Disconnected)
    override val connectionState: StateFlow<ConnectionState>
        get() = _connectionState

    private val _messages = MutableSharedFlow<MessageResponse>()
    override val messages: SharedFlow<MessageResponse>
        get() = _messages

    private val _onRemoveChat = MutableSharedFlow<RemoveChatMsg>()
    override val onRemoveChat: SharedFlow<RemoveChatMsg>
        get() = _onRemoveChat


    private val socketOpts:IO.Options = IO.Options().apply {
        reconnection = true
        reconnectionAttempts = Int.MAX_VALUE
        reconnectionDelay = SOCKET_RECONNECT_DELAY
    }

    init {
        scope.launch {
            observeAuthToken()
        }
    }

    override suspend fun connect(){
        if (socket != null) return
        updateToken()
        socket = IO.socket(BuildConfig.BASE_URL, socketOpts)
        socket!!.off()
        subscribeOnConnectionState()
        subscribeOnMessages()
        subscribeOnRemoveChat()
        socket!!.connect()
    }

    override fun disconnect(){
        socket?.disconnect()
        socket?.off()
        socket = null
        _connectionState.value = ConnectionState.Disconnected
    }

    private suspend fun updateToken(token:String? = null){
        socketOpts.extraHeaders = mapOf("Authorization" to listOf(
            "$AUTH_TOKEN_TYPE ${token ?: sessionManager.authToken.first()}"
        ))
    }

    private suspend fun observeAuthToken(){
        sessionManager.authToken.collect(){ token ->
            updateToken(token)
        }
    }

    private fun subscribeOnConnectionState(){
        if (socket == null) return
        socket!!.on(Socket.EVENT_CONNECT) { _ ->
            Log.i(TAG, "Connection state -> Connected")
            _connectionState.value = ConnectionState.Connected
        }

        socket!!.on(Socket.EVENT_DISCONNECT) { _ ->
            Log.i(TAG, "Connection state -> Disconnected")
            _connectionState.value = ConnectionState.Disconnected
        }

        socket!!.on(Socket.EVENT_CONNECT_ERROR) { e ->
            val msg = e.firstOrNull()?.toString()
            Log.i(TAG, "Connection state -> Error: $msg")
            val model = gson.toModelOrNull(msg, SocketErrorMsg::class.java)
            model?.message?.let { message ->
                if (message.equals(INVALID_AUTH_TOKEN_MSG_ERROR, ignoreCase = true)){
                    scope.launch {
                        refreshTokenRepository.refreshToken()?.let { token ->
                            Log.i(TAG, "NEW_TOKEN -> $token")
                            updateToken(token)
                            socket?.disconnect()?.connect()
                        }
                    }
                }
            }
        }
    }


    private fun subscribeOnMessages(){
        if (socket == null) return
        socket!!.on(MESSAGE_ROOM){ args ->
            val msg = args.firstOrNull()?.toString()
            Log.i(TAG, "NEW_MESSAGE -> $msg")
            gson.toModelOrNull(msg, MessageResponse::class.java)?.let { model ->
                scope.launch {
                    _messages.emit(model)
                }
            }
        }
    }

    private fun subscribeOnRemoveChat(){
        if (socket == null) return
        socket!!.on(REMOVE_CHAT_ROOM){ args ->
            val msg = args.firstOrNull()?.toString()
            Log.i(TAG, "REMOVE_CHAT -> $msg")
            gson.toModelOrNull(msg, RemoveChatMsg::class.java)?.let { model ->
                scope.launch {
                    _onRemoveChat.emit(model)
                }
            }
        }
    }


    companion object{
        private const val TAG = "SocketManager"

        private const val INVALID_AUTH_TOKEN_MSG_ERROR = "Token has expired or is invalid"

        private const val MESSAGE_ROOM = "messages"
        private const val REMOVE_CHAT_ROOM = "remove_chat"
    }
}