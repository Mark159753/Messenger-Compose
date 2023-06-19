package com.example.messenger.ui.chat.state

sealed interface ChatEvents{
    object OnScrollToFirstItem:ChatEvents
    object OnNewMessage:ChatEvents
    data class OnError(val msg:String):ChatEvents
}