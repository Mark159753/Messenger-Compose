package com.example.messenger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.messenger.ui.theme.MessengerTheme
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel:MainViewModel by viewModels()

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        val isAuth = runBlocking { viewModel.isAuth.first() }
        setTheme(R.style.AppTheme)
        setContent {
            val navController = rememberAnimatedNavController()

            val isAuthState by viewModel.isAuth.collectAsStateWithLifecycle(initialValue = isAuth)

            MessengerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MessengerNavigation(
                        modifier = Modifier.fillMaxSize(),
                        startDestination = getStartDestination(isAuthState),
                        navController = navController,
                        viewModel = viewModel
                    )
                }
            }
        }
    }

    private fun getStartDestination(isAuth:Boolean):Screen{
        return if (isAuth) Screen.Home else Screen.Start
    }
}
