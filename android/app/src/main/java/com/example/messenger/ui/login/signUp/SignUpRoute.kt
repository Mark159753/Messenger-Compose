package com.example.messenger.ui.login.signUp

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.messenger.R
import com.example.messenger.ui.common.StepsProgressIndicator
import com.example.messenger.ui.login.signUp.state.SignUpEvents
import com.example.messenger.ui.login.signUp.state.SignUpIntents
import com.example.messenger.ui.login.signUp.state.SignUpState
import com.example.messenger.ui.login.signUp.state.SignUpStep1State
import com.example.messenger.ui.login.signUp.state.SignUpStep2State
import com.example.messenger.ui.login.signUp.state.SignUpStep3State
import com.example.messenger.ui.login.signUp.state.SignUpSteps
import kotlinx.coroutines.flow.collectLatest

private const val CONTENT_ANIMATION_DURATION = 300

@Composable
fun SignUpRoute(
    viewModel: SignUpViewModel,
    onBackPressed:()->Unit = {},
    onNavToHome:()->Unit = {}
){

    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val context = LocalContext.current

    val onBackClick = {
        if (state.currentStep == SignUpSteps.One)
            onBackPressed()
        else
            viewModel.onSignUpIntent(SignUpIntents.OnNavToBackClick)
    }

    BackHandler(enabled = true, onBackClick)

    LaunchedEffect(key1 = Unit){
        viewModel.events.collectLatest { event ->
            when(event){
                is SignUpEvents.Error ->{
                    Toast.makeText(context, event.msg, Toast.LENGTH_LONG).show()
                }
                SignUpEvents.NavToHome -> onNavToHome()
            }
        }
    }

    SignUpScreen(
        state = state,
        step1State = viewModel.step1State,
        step2State = viewModel.step2State,
        step3State = viewModel.step3State,
        onBackPressed = {
            onBackClick()
        },
        onAction = viewModel::onSignUpIntent
    )
}


@Composable
fun SignUpScreen(
    state: SignUpState = remember { SignUpState() },
    step1State: SignUpStep1State = remember { SignUpStep1State() },
    step2State: SignUpStep2State = remember { SignUpStep2State() },
    step3State: SignUpStep3State = remember { SignUpStep3State() },
    onBackPressed:()->Unit = {},
    onAction:(action: SignUpIntents)->Unit = {}
){
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primaryContainer)
    ){

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(66.dp)
        ){
            IconButton(
                onClick = onBackPressed,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopStart)
            ) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "")
            }

            StepsProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .align(Alignment.Center),
                selectedStep = state.currentStep.position
            )
        }

        Box(
            modifier = Modifier
                .padding(top = 66.dp)
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(MaterialTheme.colorScheme.surface)
        ){

            AnimatedContent(
                targetState = state.currentStep,
                transitionSpec = {
                    if (targetState.position > initialState.position) {
                        // Going forwards
                        slideInHorizontally(
                            animationSpec = tween(CONTENT_ANIMATION_DURATION),
                            initialOffsetX = { fullWidth -> fullWidth }
                        ) togetherWith
                                slideOutHorizontally(
                                    animationSpec = tween(CONTENT_ANIMATION_DURATION),
                                    targetOffsetX = { fullWidth -> -fullWidth }
                                )
                    } else {
                        // Going back
                        slideInHorizontally(
                            animationSpec = tween(CONTENT_ANIMATION_DURATION),
                            initialOffsetX = { fullWidth -> -fullWidth }
                        ) togetherWith
                                slideOutHorizontally(
                                    animationSpec = tween(CONTENT_ANIMATION_DURATION),
                                    targetOffsetX = { fullWidth -> fullWidth }
                                )
                    }
                }
            ) { targetPage ->
                when(targetPage){
                    SignUpSteps.One -> SignUpStep1(
                        modifier = Modifier,
                        state = step1State
                    )
                    SignUpSteps.Two -> SignUpStep2(
                        modifier = Modifier,
                        state = step2State
                    )
                    SignUpSteps.Three -> SignUpStep3(
                        modifier = Modifier,
                        state2 = step2State,
                        state = step3State
                    )
                }
            }

            Button(
                onClick = {
                    onAction(
                        SignUpIntents.OnNextBtnClicked
                    )
                },
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 28.dp)
            ) {
                Text(text = stringResource(
                    id = if (state.currentStep == SignUpSteps.Three) R.string.start_screen_sign_up_btn
                    else R.string.sign_up_screen_next_btn
                ))
            }

            if (state.isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth(),
                )
            }
        }


    }
}


@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview(){
    Surface(modifier = Modifier.fillMaxSize()) {
        SignUpScreen()
    }
}