package com.example.messenger.domain.validators

import com.example.messenger.R
import com.example.messenger.domain.validators.base.IValidator
import com.example.messenger.domain.validators.base.ValidateResult
import com.example.messenger.ui.login.signUp.state.TextFieldState

class RepeatPasswordValidator(private val newPassword:TextFieldState):IValidator {

    override fun validate(input: String): ValidateResult {
        if (input != newPassword.text) return ValidateResult(false, R.string.text_validation_error_passwords_not_match)
        return ValidateResult(true, null)
    }
}