package com.example.messenger.ui.login.signUp.state

sealed interface SignUpIntents{
    object OnNextBtnClicked:SignUpIntents
    object OnNavToBackClick:SignUpIntents
}