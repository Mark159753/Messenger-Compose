package com.example.messenger.ui.login.signIn

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.messenger.common.UNKNOWN_ERROR
import com.example.messenger.data.workers.FetchUserWorker
import com.example.messenger.data.workers.SignInWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    @ApplicationContext
    private val context: Context
):ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading:StateFlow<Boolean>
        get() = _isLoading

    private val _uiState = MutableStateFlow(SignInState())
    val uiState:StateFlow<SignInState>
        get() = _uiState

    private val _events = Channel<SignInEvents>()
    val events = _events.receiveAsFlow()

    private val workManager = WorkManager.getInstance(context)
    private val workInfo = workManager
        .getWorkInfosForUniqueWorkLiveData(SIGN_IN_WORKER)
        .asFlow()

    init {
        observeWorker()
    }

    fun login(){
        if (uiState.value.isValid()){
            runLoginWorker()
        }
    }

    private fun runLoginWorker(){
        workManager
            .beginUniqueWork(
                SIGN_IN_WORKER,
                ExistingWorkPolicy.KEEP,
                createWorkerRequest()
            )
            .then(FetchUserWorker.createOneTimeWorkRequest())
            .enqueue()
    }

    private fun createWorkerRequest(): OneTimeWorkRequest {
        return SignInWorker.createOneTimeWorkRequest(
            uiState.value.email.text,
            uiState.value.password.text
        )
    }

    private fun observeWorker(){
        viewModelScope.launch {
            workInfo.collectLatest { info ->
                if (info.isEmpty()) return@collectLatest
                val signInWorker = info.firstOrNull{ it.tags.contains(SignInWorker::class.java.name) }
                val fetchUserWorker = info.firstOrNull{ it.tags.contains(FetchUserWorker::class.java.name) }

                _isLoading.value = info.any { it.state == WorkInfo.State.RUNNING }

                if (info.any { it.state == WorkInfo.State.FAILED }){
                    val msg = signInWorker?.outputData?.getString(SignInWorker.ERROR_MSG) ?:
                    fetchUserWorker?.outputData?.getString(FetchUserWorker.ERROR_MSG)
                    viewModelScope.launch {
                        workManager.pruneWork()
                        _events.send(
                            SignInEvents.Error(
                                msg = msg ?: UNKNOWN_ERROR
                            )
                        )
                    }
                }
                if (info.all { it.state == WorkInfo.State.SUCCEEDED }){
                    workManager.pruneWork()
                    viewModelScope.launch {
                        _events.send(
                            SignInEvents.NavToHome
                        )
                    }
                }
            }
        }
    }

    companion object{
         private const val SIGN_IN_WORKER = "com.example.messenger.ui.login.signIn.SIGN_IN_WORKER"
    }

}