package com.example.messenger.ui.start

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.messenger.R
import com.example.messenger.ui.common.placeVerticallyRelative

@Composable
fun StartRoute(
    onLoginNav:()->Unit = {},
    onSignUpNav:()->Unit = {}
){
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ){
        Image(
            painter = painterResource(id = R.drawable.start_screen_bg_figure),
            contentDescription = stringResource(id = R.string.start_screen_back_image_description),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.62f)
                .align(Alignment.TopCenter),
            contentScale = ContentScale.FillBounds,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.surfaceVariant)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            MessengerLogo(
                modifier = Modifier
                    .align(Alignment.CenterStart)
            )

            TextButton(
                onClick = onLoginNav,
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Text(text = stringResource(id = R.string.start_screen_login_btn))
            }
        }

        Text(
            text = stringResource(id = R.string.start_screen_get_start_msg),
            style = MaterialTheme.typography.displayMedium,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .placeVerticallyRelative(0.56f)
        )

        Button(
            onClick = onSignUpNav,
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .align(Alignment.BottomCenter)
                .padding(bottom = 28.dp)
        ) {
            Text(text = stringResource(id = R.string.start_screen_sign_up_btn))
        }
    }
}

@Composable
private fun MessengerLogo(
    modifier: Modifier
){
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.splash_icon),
            contentDescription = stringResource(id = R.string.start_screen_logo_image_description),
            modifier = Modifier
                .size(34.dp)
        )

        Text(
            text = stringResource(id = R.string.app_name),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun StartRoutePreview(){
    Surface(modifier = Modifier.fillMaxSize()) {
        StartRoute()
    }
}