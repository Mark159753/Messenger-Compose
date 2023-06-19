package com.example.messenger.ui.login.signUp

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.messenger.common.UNKNOWN_ERROR
import com.example.messenger.common.UNSUBSCRIBE_TIMEOUT
import com.example.messenger.data.network.models.signUp.dto.SignUpDto
import com.example.messenger.data.workers.FetchUserWorker
import com.example.messenger.data.workers.SendSignUpWorker
import com.example.messenger.data.workers.SignInWorker
import com.example.messenger.ui.login.signUp.state.SignUpEvents
import com.example.messenger.ui.login.signUp.state.SignUpIntents
import com.example.messenger.ui.login.signUp.state.SignUpState
import com.example.messenger.ui.login.signUp.state.SignUpStep1State
import com.example.messenger.ui.login.signUp.state.SignUpStep2State
import com.example.messenger.ui.login.signUp.state.SignUpStep3State
import com.example.messenger.ui.login.signUp.state.SignUpSteps
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SignUpViewModel @Inject constructor(
    @ApplicationContext
    private val context: Context
):ViewModel() {

    private val isLoading = MutableStateFlow(false)

    private val currentStep = MutableStateFlow(SignUpSteps.One)

    val step1State = SignUpStep1State()
    val step2State = SignUpStep2State()
    val step3State = SignUpStep3State()

    val uiState = combine(isLoading, currentStep){
            isLoading, currentStep ->
        SignUpState(
            currentStep = currentStep,
            isLoading = isLoading
        )
    }.stateIn(
        viewModelScope,
        started = SharingStarted.WhileSubscribed(UNSUBSCRIBE_TIMEOUT),
        initialValue = SignUpState()
    )

    private val _events = Channel<SignUpEvents>()
    val events = _events.receiveAsFlow()

    private val workManager = WorkManager.getInstance(context)
    private val workInfo = workManager
        .getWorkInfosForUniqueWorkLiveData(SIGN_UP_WORKER)
        .asFlow()

    init {
        observeWorkState()
    }

    fun onSignUpIntent(action: SignUpIntents){
        when(action){
            SignUpIntents.OnNextBtnClicked -> {
                when(currentStep.value){
                    SignUpSteps.One -> {
                        if (step1State.isValid())
                            currentStep.value = SignUpSteps.Two
                    }
                    SignUpSteps.Two -> {
                        if (step2State.isValid())
                            currentStep.value =  SignUpSteps.Three
                    }
                    SignUpSteps.Three -> sendToBack()
                }
            }
            SignUpIntents.OnNavToBackClick -> {
                currentStep.value = when(currentStep.value){
                    SignUpSteps.One -> SignUpSteps.One
                    SignUpSteps.Two -> SignUpSteps.One
                    SignUpSteps.Three -> SignUpSteps.Two
                }
            }
        }
    }

    private fun sendToBack(){
        workManager
            .beginUniqueWork(
                SIGN_UP_WORKER,
                ExistingWorkPolicy.KEEP,
                createWorkerRequest()
            )
            .then(FetchUserWorker.createOneTimeWorkRequest())
            .enqueue()
    }

    private fun observeWorkState(){
        viewModelScope.launch {
            workInfo.collectLatest { workInfo ->

                if (workInfo.isEmpty()) return@collectLatest
                val signUpWorker = workInfo.firstOrNull{ it.tags.contains(SendSignUpWorker::class.java.name) }
                val fetchUserWorker = workInfo.firstOrNull{ it.tags.contains(FetchUserWorker::class.java.name) }

                isLoading.value = workInfo.any { it.state == WorkInfo.State.RUNNING }

                if (workInfo.any { it.state == WorkInfo.State.FAILED }){
                    val msg = signUpWorker?.outputData?.getString(SignInWorker.ERROR_MSG) ?:
                    fetchUserWorker?.outputData?.getString(FetchUserWorker.ERROR_MSG)
                    viewModelScope.launch {
                        workManager.pruneWork()
                        _events.send(
                            SignUpEvents.Error(
                                msg = msg ?: UNKNOWN_ERROR
                            )
                        )
                    }
                }
                if (workInfo.all { it.state == WorkInfo.State.SUCCEEDED }){
                    viewModelScope.launch {
                        workManager.pruneWork()
                        _events.send(
                            SignUpEvents.NavToHome
                        )
                    }
                }
            }
        }
    }

    private fun createWorkerRequest(): OneTimeWorkRequest {
        val dto = SignUpDto(
            email = step1State.email.text,
            password = step1State.password.text,
            first_name = step2State.firstName.text,
            last_name = step2State.lastName.text,
            nick_name = step2State.nickName.text,
            phone = step1State.phone.text
        )
        return SendSignUpWorker
            .createOneTimeWorkRequest(dto, step3State.avatar.value)
    }

    companion object{
        private const val SIGN_UP_WORKER = "com.example.messenger.ui.login.signUp.SIGN_UP_WORKER"
    }
}