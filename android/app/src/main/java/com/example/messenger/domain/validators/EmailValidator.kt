package com.example.messenger.domain.validators

import android.text.TextUtils
import com.example.messenger.R
import com.example.messenger.domain.validators.base.IValidator
import com.example.messenger.domain.validators.base.ValidateResult

class EmailValidator() : IValidator {
    override fun validate(input:String): ValidateResult {
        val isValid =
            !TextUtils.isEmpty(input) && android.util.Patterns.EMAIL_ADDRESS.matcher(input)
                .matches()
        return ValidateResult(
            isValid,
            if (isValid) null else R.string.text_validation_error_email
        )
    }
}