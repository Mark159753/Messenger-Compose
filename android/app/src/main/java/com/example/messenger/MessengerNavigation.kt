package com.example.messenger

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import com.google.accompanist.navigation.animation.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.messenger.ui.chat.ChatRoute
import com.example.messenger.ui.common.SharedZAxisEnterTransition
import com.example.messenger.ui.common.SharedZAxisExitTransition
import com.example.messenger.ui.drawer.DrawerDestination
import com.example.messenger.ui.drawer.MessengerDrawer
import com.example.messenger.ui.drawer.MyDrawerState
import com.example.messenger.ui.drawer.MyDrawerValue
import com.example.messenger.ui.drawer.rememberDrawerState
import com.example.messenger.ui.home.HomeRoute
import com.example.messenger.ui.home.LogoutDialog
import com.example.messenger.ui.login.signIn.SignInRoute
import com.example.messenger.ui.login.signUp.SignUpRoute
import com.example.messenger.ui.search.SearchRoute
import com.example.messenger.ui.settings.SettingsRoute
import com.example.messenger.ui.start.StartRoute
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

sealed class Screen(val route: String){

    object Start: Screen("start")
    object SignUp: Screen("sign_up")
    object SignIn: Screen("sign_in")
    object Home: Screen("home")
    object Settings: Screen("settings")
    object Search: Screen("search")
    object Chat: Screen("chat/{chat_args}?chatId={chatId}"){
        fun setArgs(args:String, chatId:String?) =
            "chat/$args" + (chatId?.let { "?chatId=$it" } ?: "")
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MessengerNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberAnimatedNavController(),
    startDestination: Screen = Screen.Start,
    viewModel: MainViewModel
){
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val drawerState: MyDrawerState = rememberDrawerState(initialValue = MyDrawerValue.Closed)
    val drawerOffset = with(LocalDensity.current) { 240.dp.toPx() }
    val isDrawerEnabled = navBackStackEntry?.destination?.route == Screen.Home.route


    val user by viewModel.user.collectAsStateWithLifecycle(initialValue = null)

    val logoutDialogState = remember { mutableStateOf(false) }

    LogoutDialog(
        dialogState = logoutDialogState,
        onConfirm = viewModel::logout
    )

    LaunchedEffect(key1 = isDrawerEnabled){
        if (!isDrawerEnabled && drawerState.isOpen){
            drawerState.close()
        }
    }

    MessengerDrawer(
        data = user,
        drawerState = drawerState,
        gesturesEnabled = isDrawerEnabled,
        onNavigate = { destination ->
            when(destination){
                is DrawerDestination.NavScreen -> navController.navigate(destination.screen.route)
                DrawerDestination.OnLogoutDialog -> {
                    logoutDialogState.value = true
                }
            }
        }
    ) {

        AnimatedNavHost(
            navController = navController,
            startDestination = startDestination.route,
            modifier = modifier.offset {
                IntOffset(
                    x = drawerOffset.toInt() + drawerState.offset.value.toInt(),
                    y = 0
                )
            }
        ) {
            composable(route = Screen.Start.route) { backStackEntry ->
                StartRoute(
                    onLoginNav = {
                        if (backStackEntry.lifecycleIsResumed()) {
                            navController.navigate(Screen.SignIn.route)
                        }
                    },
                    onSignUpNav = {
                        if (backStackEntry.lifecycleIsResumed()) {
                            navController.navigate(Screen.SignUp.route)
                        }
                    }
                )
            }

            composable(route = Screen.SignIn.route) { backStackEntry ->
                SignInRoute(
                    viewModel = hiltViewModel(),
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onNavToHome = {
                        if (backStackEntry.lifecycleIsResumed()) {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(navController.graph.id) {
                                    inclusive = true
                                }
                            }
                        }
                    }
                )
            }

            composable(route = Screen.SignUp.route) { backStackEntry ->
                SignUpRoute(
                    viewModel = hiltViewModel(),
                    onBackPressed = {
                        navController.popBackStack()
                    },
                    onNavToHome = {
                        if (backStackEntry.lifecycleIsResumed()) {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(navController.graph.id) {
                                    inclusive = true
                                }
                            }
                        }
                    }
                )
            }

            composable(
                route = Screen.Home.route,
                enterTransition = {
                    when(initialState.destination.route){
                        Screen.Search.route -> SharedZAxisEnterTransition
                        else -> null
                    }
                },
                exitTransition = {
                    when(targetState.destination.route){
                        Screen.Search.route -> SharedZAxisExitTransition
                        else -> null
                    }
                },
                popEnterTransition = {
                    when(initialState.destination.route){
                        Screen.Search.route -> SharedZAxisEnterTransition
                        else -> null
                    }
                },
                popExitTransition = {
                    when(targetState.destination.route){
                        Screen.Search.route -> SharedZAxisExitTransition
                        else -> null
                    }
                }
            ) { backStackEntry ->
                HomeRoute(
                    viewModel = hiltViewModel(),
                    onNavigate = { screen ->
                        if (backStackEntry.lifecycleIsResumed()) {
                            navController.navigate(screen.route)
                        }
                     },
                    drawerState = drawerState,
                    onNavToChat = { args, chatId ->
                        if (backStackEntry.lifecycleIsResumed()){
                            navController.navigate(Screen.Chat.setArgs(args.toUri(), chatId))
                        }
                    }
                )
            }

            composable(route = Screen.Settings.route) {
                SettingsRoute()
            }
            composable(
                route = Screen.Search.route,
                enterTransition = {
                    when(initialState.destination.route){
                        Screen.Home.route -> SharedZAxisEnterTransition
                        else -> null
                    }
                },
                exitTransition = {
                    when(targetState.destination.route){
                        Screen.Home.route -> SharedZAxisExitTransition
                        else -> null
                    }
                },
                popEnterTransition = {
                    when(initialState.destination.route){
                        Screen.Home.route -> SharedZAxisEnterTransition
                        else -> null
                    }
                },
                popExitTransition = {
                    when(targetState.destination.route){
                        Screen.Home.route -> SharedZAxisExitTransition
                        else -> null
                    }
                }
            ) { backStackEntry ->
                SearchRoute(
                    viewModel = hiltViewModel(),
                    onNavBack = {
                        if (backStackEntry.lifecycleIsResumed()){
                            navController.popBackStack()
                        }
                    },
                    onNavToChat = { args, chatId:String? ->
                        if (backStackEntry.lifecycleIsResumed()){
                            navController.navigate(Screen.Chat.setArgs(args.toUri(), chatId)){
                                popUpTo(Screen.Home.route)
                            }
                        }
                    }
                )
            }
            composable(
                route = Screen.Chat.route,
                arguments = mutableListOf(
                    navArgument("chat_args") {
                        type = NavType.StringType
                    },
                    navArgument("chatId") {
                        nullable = true
                        defaultValue = null
                    }
                )
            ){ backStackEntry ->
                ChatRoute(
                    viewModel = hiltViewModel(),
                    onNavBack = {
                        if (backStackEntry.lifecycleIsResumed()){
                            navController.popBackStack()
                        }
                    }
                )
            }
        }
    }

}


private fun NavBackStackEntry.lifecycleIsResumed() =
    this.lifecycle.currentState == Lifecycle.State.RESUMED