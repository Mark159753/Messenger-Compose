package com.example.messenger.ui.login.signIn

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.messenger.R
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SignInRoute(
    viewModel: SignInViewModel,
    onBackClick:()->Unit = {},
    onNavToHome:()->Unit = {}
){

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    val context = LocalContext.current

    LaunchedEffect(key1 = Unit){
        viewModel.events.collectLatest { event ->
            when(event){
                is SignInEvents.Error -> Toast.makeText(context, event.msg, Toast.LENGTH_LONG).show()
                SignInEvents.NavToHome -> onNavToHome()
            }
        }
    }

    SignInScreen(
        state = state,
        isLoading = isLoading,
        onBackClick = onBackClick,
        onLogIn = viewModel::login
    )

}

@Composable
fun SignInScreen(
    state: SignInState,
    isLoading:Boolean = false,
    onBackClick:()->Unit = {},
    onLogIn: () -> Unit = {}
){

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .padding(top = 16.dp, start = 16.dp)
                .align(Alignment.Start)
        ) {
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "")
        }

        if (isLoading) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth(),
            )
        }

        Spacer(modifier = Modifier.fillMaxHeight(0.12f))

        Image(
            painter = painterResource(id = R.drawable.splash_icon),
            contentDescription = "",
            modifier = Modifier
                .size(70.dp)
        )

        TextFields(
            modifier = Modifier
                .padding(top = 16.dp),
            state = state
        )

        TextButton(
            onClick = onLogIn,
            modifier = Modifier
                .padding(top = 20.dp)
        ) {
            Text(
                text = stringResource(id = R.string.start_screen_login_btn),
                style = MaterialTheme.typography.headlineSmall
            )
        }

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextFields(
    modifier: Modifier = Modifier,
    state: SignInState,
){

    val showPassword = remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 26.dp)
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = state.email.text,
            onValueChange = {
                state.email.text = it
            },
            label = {
                Text(text = stringResource(id = R.string.sign_in_screen_email_label))
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.email_icon),
                    contentDescription = "",
                )
            },
            placeholder = {
                Text(text = stringResource(id = R.string.sign_in_screen_email_placeholder))
            },
            maxLines = 1,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }
            ),
            isError = state.email.hasError,
            supportingText = {
                if (state.email.hasError) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(id = state.email.errorMsg!!),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
        )

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            value = state.password.text,
            onValueChange = {
                state.password.text = it
            },
            label = {
                Text(text = stringResource(id = R.string.sign_in_screen_password_label))
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.password_key_icon),
                    contentDescription = "",
                )
            },
            maxLines = 1,
            trailingIcon = {
                IconButton(onClick = { showPassword.value = !showPassword.value }) {
                    Icon(
                        painter = painterResource(id =
                        if (showPassword.value)
                            R.drawable.visibility_off_icon
                        else
                            R.drawable.visibility_icon
                        ),
                        contentDescription = "",
                        modifier = Modifier.size(28.dp)
                    )
                }
            },
            singleLine = true,
            visualTransformation = if (showPassword.value) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(
                autoCorrect = true,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                }
            ),
            isError = state.password.hasError,
            supportingText = {
                if (state.password.hasError) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(id = state.password.errorMsg!!),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SignInScreenPreview(){
    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {
        SignInScreen(
            state = SignInState()
        )
    }
}