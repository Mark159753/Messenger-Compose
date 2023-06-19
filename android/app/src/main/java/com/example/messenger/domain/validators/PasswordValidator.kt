package com.example.messenger.domain.validators

import com.example.messenger.R
import com.example.messenger.domain.validators.base.IValidator
import com.example.messenger.domain.validators.base.ValidateResult

class PasswordValidator() : IValidator {
    private val minPasswordLength = 6
    private val maxPasswordLength = 12

    override fun validate(input:String): ValidateResult {
        if (input.length < minPasswordLength)
            return ValidateResult(false, R.string.text_validation_error_min_pass_length)
        if (input.length > maxPasswordLength)
            return ValidateResult(false, R.string.text_validation_error_max_pass_length)
        return ValidateResult(true, null)
    }
}
