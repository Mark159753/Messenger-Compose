package com.example.messenger.ui.login.signUp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.messenger.R
import com.example.messenger.ui.common.PhoneNumberFilter
import com.example.messenger.ui.login.signUp.state.SignUpStep1State

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpStep1(
    modifier: Modifier = Modifier,
    state:SignUpStep1State = remember { SignUpStep1State() }
){
    val showPassword = remember { mutableStateOf(false) }
    val showRepeatPassword = remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 26.dp)
            .padding(top = 24.dp)
    ) {
        
        Text(text = stringResource(id = R.string.sign_up_step_1_title), style = MaterialTheme.typography.headlineLarge)
        Text(text = stringResource(id = R.string.sign_up_step_1_subtitle), style = MaterialTheme.typography.bodySmall)

        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 33.dp),
            value = state.phone.text,
            onValueChange = {
                if (it.length <= 9) state.phone.text = it
            },
            label = {
                Text(text = stringResource(id = R.string.sign_up_step_1_phone_label))
            },
            maxLines = 1,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            visualTransformation = PhoneNumberFilter("+380 "),
            isError = state.phone.hasError,
            supportingText = {
                if (state.phone.hasError) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(id = state.phone.errorMsg!!),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            trailingIcon = {
                if (state.phone.hasError)
                    Icon(
                        painterResource(id = R.drawable.error_icon),
                        "error",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .size(28.dp)
                    )
            },

        )

        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            value = state.email.text,
            onValueChange = {
                state.email.text = it
            },
            label = {
                Text(text = stringResource(id = R.string.sign_up_step_1_email_label))
            },
            maxLines = 1,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
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
            trailingIcon = {
                if (state.email.hasError)
                    Icon(
                        painterResource(id = R.drawable.error_icon),
                        "error",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .size(28.dp)
                    )
            },
        )

        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            value = state.password.text,
            onValueChange = {
                state.password.text = it
            },
            label = {
                Text(text = stringResource(id = R.string.sign_up_step_1_password_label))
            },
            maxLines = 1,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
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
            visualTransformation = if (showPassword.value) VisualTransformation.None else PasswordVisualTransformation(),
            isError = state.password.hasError,
            supportingText = {
                if (state.password.hasError){
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(id = state.password.errorMsg!!),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        )

        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            value = state.passwordRepeat.text,
            onValueChange = {
                state.passwordRepeat.text = it
            },
            label = {
                Text(text = stringResource(id = R.string.sign_up_step_1_repeat_password_label))
            },
            maxLines = 1,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            trailingIcon = {
                IconButton(onClick = { showRepeatPassword.value = !showRepeatPassword.value }) {
                    Icon(
                        painter = painterResource(id =
                        if (showRepeatPassword.value)
                            R.drawable.visibility_off_icon
                        else
                            R.drawable.visibility_icon
                        ),
                        contentDescription = "",
                        modifier = Modifier.size(28.dp)
                    )
                }
            },
            visualTransformation = if (showRepeatPassword.value) VisualTransformation.None else PasswordVisualTransformation(),
            isError = state.passwordRepeat.hasError,
            supportingText = {
                if (state.passwordRepeat.hasError){
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(id = state.passwordRepeat.errorMsg!!),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        )

    }
}

@Preview(showBackground = true)
@Composable
fun SignUpStep1Preview(){
    Surface(modifier = Modifier.fillMaxSize()) {
        SignUpStep1()
    }
}