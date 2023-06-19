package com.example.messenger.ui.login.signUp

import android.Manifest
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.messenger.R
import com.example.messenger.ui.login.signUp.state.SignUpStep2State
import com.example.messenger.ui.login.signUp.state.SignUpStep3State
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SignUpStep3(
    modifier: Modifier = Modifier,
    state2: SignUpStep2State = remember { SignUpStep2State() },
    state: SignUpStep3State = remember { SignUpStep3State() }
){

    val permissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(permission = Manifest.permission.READ_MEDIA_IMAGES)
    } else {
        rememberPermissionState(permission = Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    val imageLauncher = rememberLauncherForActivityResult(contract =
    ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            state.avatar.value = uri
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 26.dp)
            .padding(top = 24.dp)
    ){
        Text(text = stringResource(id = R.string.sign_up_step_3_title), style = MaterialTheme.typography.headlineLarge)
        Text(text = stringResource(id = R.string.sign_up_step_3_subtitle), style = MaterialTheme.typography.bodySmall)

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.1f)
        )


        Box(
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .align(Alignment.CenterHorizontally)
                .clickable {
                       if (permissionState.status.isGranted)
                            imageLauncher.launch("image/*")
                        else
                            permissionState.launchPermissionRequest()
                },
            contentAlignment = Alignment.Center
        ){
            Text(
                text = state2.firstName.text.firstOrNull()?.uppercase() ?: "",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (state.avatar.value != null){
                Image(
                    painter = rememberAsyncImagePainter(state.avatar.value),
                    contentDescription = "User avatar",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Text(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 16.dp),
            text = state2.getFullName(),
            style = MaterialTheme.typography.titleLarge
        )
    }

}

@Preview(showBackground = true)
@Composable
private fun SignUpStep3Preview(){
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        SignUpStep3(
            state2 = remember {
                SignUpStep2State().also {
                    it.firstName.text = "Mark"
                    it.lastName.text = "Armstrong"
                }
            }
        )
    }
}