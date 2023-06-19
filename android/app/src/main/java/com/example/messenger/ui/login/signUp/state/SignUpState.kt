package com.example.messenger.ui.login.signUp.state

data class SignUpState(
    val currentStep:SignUpSteps = SignUpSteps.One,
    val isLoading:Boolean = false
)

enum class SignUpSteps(val position:Int){
    One(1), Two(2), Three(3)
}
