package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AppColorScheme = darkColorScheme(
    primary = GoldAccent,
    onPrimary = Color.Black,
    secondary = GoldLight,
    onSecondary = Color.Black,
    background = DarkBackground,
    onBackground = Color.White,
    surface = DarkSurface,
    onSurface = Color.White,
    surfaceVariant = GoldBackground,
    onSurfaceVariant = Color.White,
    error = ErrorRed,
    onError = Color.Black
)

@Composable
fun MyApplicationTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = AppColorScheme, typography = Typography, content = content)
}
