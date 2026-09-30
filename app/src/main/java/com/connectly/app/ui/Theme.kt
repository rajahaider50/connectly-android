package com.connectly.app.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Ink = Color(0xFF0A0B12)
val Surface = Color(0xFF12131D)
val SurfaceElevated = Color(0xFF1A1B29)
val Violet = Color(0xFF8B5CF6)
val Cyan = Color(0xFF22D3EE)
val TextPrimary = Color(0xFFF8FAFC)
val TextMuted = Color(0xFF9CA3AF)

private val DarkColors = darkColorScheme(
    primary = Violet,
    secondary = Cyan,
    background = Ink,
    surface = Surface,
    surfaceVariant = SurfaceElevated,
    onPrimary = Color.White,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextMuted
)

@Composable fun ConnectlyTheme(content: @Composable () -> Unit) { MaterialTheme(colorScheme = DarkColors, typography = Typography(), content = content) }
