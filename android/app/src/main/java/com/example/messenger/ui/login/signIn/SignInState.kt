package com.example.messenger.ui.login.signIn

import com.example.messenger.domain.validators.EmailValidator
import com.example.messenger.domain.validators.EmptyValidator
import com.example.messenger.domain.validators.PasswordValidator
import com.example.messenger.ui.login.signUp.state.TextFieldState
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.starProjectedType

data class SignInState(
    val email: TextFieldState = TextFieldState(EmptyValidator(), EmailValidator()),
    val password: TextFieldState = TextFieldState(EmptyValidator(), PasswordValidator())
){

    fun isValid(): Boolean {
        return !SignInState::class.memberProperties
            .filter { it.returnType == TextFieldState::class.starProjectedType }
            .map { it.get(this) as TextFieldState }
            .map { it.isValid() }
            .any { !it }
    }
}


sealed interface SignInEvents{
    object NavToHome:SignInEvents
    data class Error(val msg:String):SignInEvents
}
