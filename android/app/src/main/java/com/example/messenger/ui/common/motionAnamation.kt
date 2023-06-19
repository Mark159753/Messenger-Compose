package com.example.messenger.ui.common

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp


val SharedXAxisEnterTransition: (Density) -> EnterTransition = { density ->
    fadeIn(animationSpec = tween(durationMillis = 210, delayMillis = 90, easing = LinearOutSlowInEasing)) +
            slideInHorizontally(animationSpec = tween(durationMillis = 300)) {
                with(density) { 30.dp.roundToPx() }
            }
}

val SharedXAxisExitTransition: (Density) -> ExitTransition = { density ->
    fadeOut(animationSpec = tween(durationMillis = 90, easing = FastOutLinearInEasing)) +
            slideOutHorizontally(animationSpec = tween(durationMillis = 300)) {
                with(density) { (-30).dp.roundToPx() }
            }
}

val SharedYAxisEnterTransition: (Density) -> EnterTransition = { density ->
    fadeIn(animationSpec = tween(durationMillis = 210, delayMillis = 90, easing = LinearOutSlowInEasing)) +
            slideInVertically(animationSpec = tween(durationMillis = 300)) {
                with(density) { 30.dp.roundToPx() }
            }
}

val SharedYAxisExitTransition: (Density) -> ExitTransition = { density ->
    fadeOut(animationSpec = tween(durationMillis = 90, easing = FastOutLinearInEasing)) +
            slideOutVertically(animationSpec = tween(durationMillis = 300)) {
                with(density) { (-30).dp.roundToPx() }
            }
}

@ExperimentalAnimationApi
val SharedZAxisEnterTransition =
    fadeIn(animationSpec = tween(durationMillis = 210, delayMillis = 90, easing = LinearOutSlowInEasing)) +
            scaleIn(initialScale = 0.8f, animationSpec = tween(durationMillis = 300))

@ExperimentalAnimationApi
val SharedZAxisVariantEnterTransition =
    fadeIn(animationSpec = tween(durationMillis = 60, delayMillis = 60, easing = LinearEasing)) +
            scaleIn(initialScale = 0.8f, animationSpec = tween(durationMillis = 300))

@ExperimentalAnimationApi
val SharedZAxisExitTransition =
    fadeOut(animationSpec = tween(durationMillis = 90, easing = FastOutLinearInEasing)) +
            scaleOut(targetScale = 1.1f, animationSpec = tween(durationMillis = 300))

@ExperimentalAnimationApi
val SharedZAxisVariantExitTransition = scaleOut(targetScale = 1.1f, animationSpec = tween(durationMillis = 300))

@ExperimentalAnimationApi
val FadeThroughEnterTransition =
    fadeIn(animationSpec = tween(durationMillis = 210, delayMillis = 90, easing = LinearOutSlowInEasing)) +
            scaleIn(initialScale = 0.92f, animationSpec = tween(durationMillis = 210, delayMillis = 90, easing = LinearOutSlowInEasing))

val FadeThroughExitTransition = fadeOut(animationSpec = tween(durationMillis = 90, easing = FastOutLinearInEasing))

@ExperimentalAnimationApi
val FadeEnterTransition =
    fadeIn(animationSpec = tween(durationMillis = 45, easing = LinearEasing)) +
            scaleIn(initialScale = 0.8f, animationSpec = tween(durationMillis = 150, easing = LinearOutSlowInEasing))

val FadeExitTransition = fadeOut(animationSpec = tween(durationMillis = 75, easing = LinearEasing))