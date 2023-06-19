package com.example.messenger.ui.login.signUp.state

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

data class SignUpStep3State(
    val avatar: MutableState<Uri?> = mutableStateOf( null )
)
