package com.example.messenger.ui.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview


@Composable
fun SettingsRoute(){
    Box(
        modifier = Modifier
                .fillMaxSize(),
        contentAlignment = Alignment.Center
    ){

        Text(text = "Settings")
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsRoutePreview(){
    Surface(modifier = Modifier.fillMaxSize()) {
        SettingsRoute()
    }
}