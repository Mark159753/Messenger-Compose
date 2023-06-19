package com.example.messenger.ui.login.signUp.state

import com.example.messenger.domain.validators.EmailValidator
import com.example.messenger.domain.validators.EmptyValidator
import com.example.messenger.domain.validators.PasswordValidator
import com.example.messenger.domain.validators.PhoneValidator
import com.example.messenger.domain.validators.RepeatPasswordValidator
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.starProjectedType

data class SignUpStep1State(
    val phone:TextFieldState = TextFieldState(EmptyValidator(), PhoneValidator()),
    val email:TextFieldState = TextFieldState(EmptyValidator(), EmailValidator()),
    val password:TextFieldState = TextFieldState(EmptyValidator(), PasswordValidator()),
    val passwordRepeat:TextFieldState = TextFieldState(EmptyValidator(), RepeatPasswordValidator(password))
){

    fun isValid(): Boolean {
        return !SignUpStep1State::class.memberProperties
            .filter { it.returnType == TextFieldState::class.starProjectedType }
            .map { it.get(this) as TextFieldState }
            .map { it.isValid() }
            .any { !it }
    }
}
