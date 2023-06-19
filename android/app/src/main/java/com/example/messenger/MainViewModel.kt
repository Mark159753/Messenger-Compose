package com.example.messenger

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messenger.data.local.proto.user.UserLocalDataSource
import com.example.messenger.data.local.session.SessionManager
import com.example.messenger.data.network.socket.SocketManager
import com.example.messenger.data.network.socket.SocketManagerImpl
import com.example.messenger.data.repository.auth.AuthRepository
import com.example.messenger.domain.chat.OnRemoveChat
import com.example.messenger.domain.message.OnNewMessage
import com.example.messenger.ui.drawer.DrawerHeaderData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val authRepository: AuthRepository,
    private val localUserLocalDataSource: UserLocalDataSource,
    private val socketManager: SocketManager,
    private val onNewMessage: OnNewMessage,
    private val onRemoveChat: OnRemoveChat
): ViewModel() {

    val isAuth = sessionManager
        .authToken
        .map { !it.isNullOrBlank() }

    val user = localUserLocalDataSource
        .userPreferencesFlow
        .map {user ->
            DrawerHeaderData(
                name = "${user.firstName} ${user.lastName}",
                avatar = user.avatar,
                email = user.email
            )
        }

    init {
        manageSocketConnection()
        subscribeOnSocket()
    }

    fun logout(){
        viewModelScope.launch {
            authRepository.logout()
        }
    }

    private fun manageSocketConnection(){
        viewModelScope.launch {
            isAuth.collectLatest { isAuth ->
                if (isAuth)
                    socketManager.connect()
                else
                    socketManager.disconnect()
            }
        }
    }

    private fun subscribeOnSocket(){
        viewModelScope.launch {
            socketManager
                .messages
                .collect(){ msg ->
                    onNewMessage(msg)
                }
        }

        viewModelScope.launch {
            socketManager
                .onRemoveChat
                .collect(){ chat ->
                    onRemoveChat(chat)
                }
        }
    }

    override fun onCleared() {
        socketManager.disconnect()
        super.onCleared()
    }

}