package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = ElegantBlue,
    onPrimary = Color.White,
    primaryContainer = ElegantBlueDark,
    onPrimaryContainer = Color.White,
    secondary = ElegantGreen,
    onSecondary = Color.White,
    secondaryContainer = ElegantGreenSubtle,
    onSecondaryContainer = ElegantGreen,
    tertiary = ElegantBlue,
    background = ElegantCanvas,
    onBackground = TextPrimary,
    surface = ElegantSurface,
    onSurface = TextPrimary,
    surfaceVariant = ElegantSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = ElegantBorder,
    outlineVariant = ElegantBorderSubtle,
    error = ErrorRed
)

private val LightColorScheme = lightColorScheme(
    primary = EmeraldDark,
    onPrimary = Color.White,
    secondary = MoonGoldDark,
    onSecondary = Color.Black,
    background = Color(0xFFF1F5F9),
    onBackground = Color(0xFF0F172A),
    surface = Color.White,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFFCBD5E1),
    error = ErrorRed
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to luxury dark fintech aesthetic
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
