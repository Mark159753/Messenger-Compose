package com.example.messenger.ui.login.signUp.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.messenger.domain.validators.base.IValidator

class TextFieldState(vararg validators:IValidator) {

    private val validatorsArray = validators

    var text by mutableStateOf("")
    var errorMsg:Int? by mutableStateOf(null)
    var hasError:Boolean by mutableStateOf(errorMsg != null)

    fun isValid():Boolean{
        for (validator in validatorsArray) {
            val result = validator.validate(text)
            if (!result.isSuccess) {
                errorMsg = result.message
                hasError = true
                return false
            }
        }
        hasError = false
        errorMsg = null
        return true
    }
}