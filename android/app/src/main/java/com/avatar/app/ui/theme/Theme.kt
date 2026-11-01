package com.avatar.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = KasAppOlive,
    onPrimary = Color.White,
    primaryContainer = KasAppDarkOlive,
    onPrimaryContainer = Color.White,
    secondary = KasAppLightOlive,
    tertiary = KasAppCyan,
    background = KasAppBg,
    surface = SurfaceWhite,
    error = ErrorRed,
)

@Composable
fun AvatarTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}
