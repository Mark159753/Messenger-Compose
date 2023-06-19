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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.messenger.R
import com.example.messenger.ui.login.signUp.state.SignUpStep2State


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpStep2(
    modifier: Modifier = Modifier,
    state: SignUpStep2State = remember { SignUpStep2State() },
){
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 26.dp)
            .padding(top = 24.dp)
    ){
        Text(text = stringResource(id = R.string.sign_up_step_2_title), style = MaterialTheme.typography.headlineLarge)
        Text(text = stringResource(id = R.string.sign_up_step_2_subtitle), style = MaterialTheme.typography.bodySmall)

        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 33.dp),
            value = state.firstName.text,
            onValueChange = {
                state.firstName.text = it
            },
            label = {
                Text(text = stringResource(id = R.string.sign_up_step_2_first_name_label))
            },
            maxLines = 1,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
                capitalization = KeyboardCapitalization.Sentences
            ),
            isError = state.firstName.hasError,
            supportingText = {
                if (state.firstName.hasError) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(id = state.firstName.errorMsg!!),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            trailingIcon = {
                if (state.firstName.hasError)
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
            value = state.lastName.text,
            onValueChange = {
                state.lastName.text = it
            },
            label = {
                Text(text = stringResource(id = R.string.sign_up_step_2_last_name_label))
            },
            maxLines = 1,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
                capitalization = KeyboardCapitalization.Sentences
            ),
            isError = state.lastName.hasError,
            supportingText = {
                if (state.lastName.hasError) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(id = state.lastName.errorMsg!!),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            trailingIcon = {
                if (state.lastName.hasError)
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
            value = state.nickName.text,
            onValueChange = {
                state.nickName.text = it
            },
            label = {
                Text(text = stringResource(id = R.string.sign_up_step_2_nickname_label))
            },
            maxLines = 1,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            isError = state.nickName.hasError,
            supportingText = {
                if (state.nickName.hasError) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(id = state.nickName.errorMsg!!),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            trailingIcon = {
                if (state.nickName.hasError)
                    Icon(
                        painterResource(id = R.drawable.error_icon),
                        "error",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .size(28.dp)
                    )
            },
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun SignUpStep2Preview(){
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        SignUpStep2()
    }
}