package com.example.messenger.ui.chat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.example.messenger.common.UNKNOWN_ERROR
import com.example.messenger.data.local.db.entities.relation.MessageWithAuthor
import com.example.messenger.data.local.proto.user.UserLocalDataSource
import com.example.messenger.data.local.provider.images.Image
import com.example.messenger.data.network.adapter.NetworkResponse
import com.example.messenger.data.network.models.message.dto.MessageDto
import com.example.messenger.data.network.socket.SocketManager
import com.example.messenger.data.repository.chat.ChatRepository
import com.example.messenger.data.repository.messages.MessagesRepository
import com.example.messenger.domain.date.YYY_DD_MM
import com.example.messenger.domain.date.formatDate
import com.example.messenger.domain.message.SendMessage
import com.example.messenger.ui.chat.model.ChatArgs
import com.example.messenger.ui.chat.model.MessageItemUi
import com.example.messenger.ui.chat.model.toUi
import com.example.messenger.ui.chat.state.ChatEvents
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val messagesRepository: MessagesRepository,
    private val userLocalDataSource: UserLocalDataSource,
    private val sendMessage: SendMessage,
    private val chatRepository: ChatRepository,
    private val socketManager: SocketManager
):ViewModel() {

    private val me = userLocalDataSource.userPreferencesFlow

    var messageText by mutableStateOf("")
        private set

    private val _events = Channel<ChatEvents>()
    val events = _events.receiveAsFlow()

    val chatId = savedStateHandle
        .getStateFlow<String?>("chatId", null)

    val args = savedStateHandle
        .getStateFlow<String?>("chat_args", null)
        .filterNotNull()
        .map {
            Gson().fromJson(it, ChatArgs::class.java)
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    val messagesList = chatId
        .flatMapLatest { chatId ->
            chatId?.let { id ->
                messagesRepository.getMessages(id)
        } ?: getEmptyResult()
        }
        .combine(me){ paging, me ->
            paging.map {
                it.toUi(it.author.id != me.id)
            }.insertSeparators { first: MessageItemUi?, second: MessageItemUi? ->
                    val before = first as? MessageItemUi.MessageUiItem
                    val after = second as? MessageItemUi.MessageUiItem
                    when{
                        before == null -> null
                        after == null -> {
                            MessageItemUi.StickyHeader(id = UUID.randomUUID().toString(), title = before.message.created_at.formatDate(YYY_DD_MM))
                        }
//                        before == null || after == null -> null
                        before.message.created_at.formatDate(YYY_DD_MM) != after.message.created_at.formatDate(YYY_DD_MM) -> MessageItemUi.StickyHeader(before.message.created_at.formatDate(YYY_DD_MM), UUID.randomUUID().toString())
                        else -> null
                    }
                }
        }
        .cachedIn(viewModelScope)

    val selectedImages = mutableStateListOf<Image>()

    init {
        listenToNewMessages()
    }

    fun setImages(list: List<Image>){
        selectedImages.clear()
        selectedImages.addAll(list)
    }

    fun sendMsg(){
        if (messageText.isBlank() && selectedImages.isEmpty()) return
        viewModelScope.launch {
            val chatId = this@ChatViewModel.chatId.value
                ?: createChat()
            chatId?.let { id ->
                sendMessage(
                    MessageDto(
                        message = messageText,
                        chatId = id
                    ),
                    ArrayList(selectedImages).map { it.path }
                )
                _events.send(ChatEvents.OnScrollToFirstItem)
                messageText = ""
                selectedImages.clear()
            }
        }
    }

    fun onUpdateMessageText(msg:String){
        messageText = msg
    }

    private suspend fun createChat(): String? {
        val userId = args.firstOrNull()?.userId ?: return null
        return when(val res = chatRepository.createChat(userId)){
            is NetworkResponse.ApiError -> {
                _events.send(ChatEvents.OnError(res.body?.message ?: res.error?.message ?: UNKNOWN_ERROR))
                null
            }
            is NetworkResponse.Success -> {
                savedStateHandle["chatId"] = res.body.id
                res.body.id
            }
        }
    }

    private fun listenToNewMessages(){
        viewModelScope.launch {
            me.combine(socketManager.messages){ user, newMessage ->
                if (newMessage.author.id != user.id)
                    newMessage
                else null
            }.filterNotNull()
                .collect(){ newMessage ->
                    _events.send(ChatEvents.OnNewMessage)
                }
        }
    }

    private fun getEmptyResult() = flow<PagingData<MessageWithAuthor>> {
        emit(PagingData.empty())
    }
}