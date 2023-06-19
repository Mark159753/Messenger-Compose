package com.example.messenger.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

data class MessengerColors(
    val primaryFixed: Color = Color.Unspecified,
    val onPrimaryFixed: Color = Color.Unspecified,
    val primaryFixedDim: Color = Color.Unspecified,
    val onPrimaryFixedVariant: Color = Color.Unspecified,
    val secondaryFixed: Color = Color.Unspecified,
    val onSecondaryFixed: Color = Color.Unspecified,
    val secondaryFixedDim: Color = Color.Unspecified,
    val onSecondaryFixedVariant: Color = Color.Unspecified,
    val tertiaryFixed: Color = Color.Unspecified,
    val onTertiaryFixed: Color = Color.Unspecified,
    val tertiaryFixedDim: Color = Color.Unspecified,
    val onTertiaryFixedVariant: Color = Color.Unspecified,
    val surfaceContainerHighest: Color = Color.Unspecified,
    val surfaceContainerHigh: Color = Color.Unspecified,
    val surfaceContainer: Color = Color.Unspecified,
    val surfaceContainerLow: Color = Color.Unspecified,
    val surfaceContainerLowest: Color = Color.Unspecified,
    val surfaceDim: Color = Color.Unspecified,
    val surfaceBright: Color = Color.Unspecified
)

val LightMessengerColors = MessengerColors(
    primaryFixed = md_theme_light_primaryFixed,
    onPrimaryFixed = md_theme_light_onPrimaryFixed,
    primaryFixedDim = md_theme_light_primaryFixedDim,
    onPrimaryFixedVariant = md_theme_light_onPrimaryFixedVariant,
    secondaryFixed = md_theme_light_secondaryFixed,
    onSecondaryFixed = md_theme_light_onSecondaryFixed,
    secondaryFixedDim = md_theme_light_secondaryFixedDim,
    onSecondaryFixedVariant = md_theme_light_onSecondaryFixedVariant,
    tertiaryFixed = md_theme_light_tertiaryFixed,
    onTertiaryFixed = md_theme_light_onTertiaryFixed,
    tertiaryFixedDim = md_theme_light_tertiaryFixedDim,
    onTertiaryFixedVariant = md_theme_light_onTertiaryFixedVariant,
    surfaceContainerHighest = md_theme_light_surfaceContainerHighest,
    surfaceContainerHigh = md_theme_light_surfaceContainerHigh,
    surfaceContainer = md_theme_light_surfaceContainer,
    surfaceContainerLow = md_theme_light_surfaceContainerLow,
    surfaceContainerLowest = md_theme_light_surfaceContainerLowest,
    surfaceDim = md_theme_light_surfaceDim,
    surfaceBright = md_theme_light_surfaceBright
)

val DarkMessengerColors = MessengerColors(
    primaryFixed = md_theme_dark_primaryFixed,
    onPrimaryFixed = md_theme_dark_onPrimaryFixed,
    primaryFixedDim = md_theme_dark_primaryFixedDim,
    onPrimaryFixedVariant = md_theme_dark_onPrimaryFixedVariant,
    secondaryFixed = md_theme_dark_secondaryFixed,
    onSecondaryFixed = md_theme_dark_onSecondaryFixed,
    secondaryFixedDim = md_theme_dark_secondaryFixedDim,
    onSecondaryFixedVariant = md_theme_dark_onSecondaryFixedVariant,
    tertiaryFixed = md_theme_dark_tertiaryFixed,
    onTertiaryFixed = md_theme_dark_onTertiaryFixed,
    tertiaryFixedDim = md_theme_dark_tertiaryFixedDim,
    onTertiaryFixedVariant = md_theme_dark_onTertiaryFixedVariant,
    surfaceContainerHighest = md_theme_dark_surfaceContainerHighest,
    surfaceContainerHigh = md_theme_dark_surfaceContainerHigh,
    surfaceContainer = md_theme_dark_surfaceContainer,
    surfaceContainerLow = md_theme_dark_surfaceContainerLow,
    surfaceContainerLowest = md_theme_dark_surfaceContainerLowest,
    surfaceDim = md_theme_dark_surfaceDim,
    surfaceBright = md_theme_dark_surfaceBright
)

val LocalMessengerColors = compositionLocalOf {
    LightMessengerColors
}

val MaterialTheme.localColors: MessengerColors
    @Composable
    get() = LocalMessengerColors.current