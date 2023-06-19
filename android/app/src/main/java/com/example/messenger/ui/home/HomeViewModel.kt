package com.example.messenger.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messenger.data.local.db.entities.relation.ChatWithMessageAndUser
import com.example.messenger.data.repository.chat.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val chatRepository: ChatRepository
):ViewModel() {

    val pagingList = chatRepository.getChats()

    fun removeChat(chat: ChatWithMessageAndUser){
        viewModelScope.launch {
            chatRepository.removeChat(chat.chat.id)
        }
    }

}