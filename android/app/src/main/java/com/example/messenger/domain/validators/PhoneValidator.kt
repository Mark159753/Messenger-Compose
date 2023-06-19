package com.example.messenger.domain.validators

import androidx.core.text.isDigitsOnly
import com.example.messenger.R
import com.example.messenger.domain.validators.base.IValidator
import com.example.messenger.domain.validators.base.ValidateResult

class PhoneValidator:IValidator {

    override fun validate(input: String): ValidateResult {
        return when{
            !input.isDigitsOnly() -> ValidateResult(isSuccess = false, message = R.string.text_validation_error_phone_number_must_be_digits)
            input.length < 9 -> ValidateResult(isSuccess = false, message = R.string.text_validation_error_phone_number_short)
            else -> ValidateResult(isSuccess = true, message = null)
        }
    }
}