package com.example.messenger.ui.login.signUp.state

sealed interface SignUpEvents{
    object NavToHome:SignUpEvents
    data class Error(val msg:String):SignUpEvents
}