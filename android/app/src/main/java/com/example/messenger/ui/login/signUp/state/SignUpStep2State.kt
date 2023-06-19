package com.example.messenger.ui.login.signUp.state

import com.example.messenger.domain.validators.EmptyValidator
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.starProjectedType

data class SignUpStep2State(
    val firstName:TextFieldState = TextFieldState(EmptyValidator()),
    val lastName:TextFieldState = TextFieldState(EmptyValidator()),
    val nickName:TextFieldState = TextFieldState()
){

    fun getFullName() = "${firstName.text} ${lastName.text}"

    fun isValid(): Boolean {
        return !SignUpStep2State::class.memberProperties
            .filter { it.returnType == TextFieldState::class.starProjectedType }
            .map { it.get(this) as TextFieldState }
            .map { it.isValid() }
            .any { !it }
    }
}
