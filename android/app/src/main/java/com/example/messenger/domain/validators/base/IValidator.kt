package com.example.messenger.domain.validators.base

interface IValidator {
    fun validate(input:String) : ValidateResult
}