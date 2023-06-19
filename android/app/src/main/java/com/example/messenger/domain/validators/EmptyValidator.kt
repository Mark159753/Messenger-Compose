package com.example.messenger.domain.validators

import com.example.messenger.R
import com.example.messenger.domain.validators.base.IValidator
import com.example.messenger.domain.validators.base.ValidateResult

class EmptyValidator() : IValidator {
    override fun validate(input:String): ValidateResult {
        val isValid = input.isNotEmpty()
        return ValidateResult(
            isValid,
            if (isValid) null else R.string.text_validation_error_empty_field
        )
    }
}
