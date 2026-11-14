package com.kasipem.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val BluePrimary = Color(0xFF0284C7)
private val TealPrimary = Color(0xFF0D9488)
private val EmeraldPrimary = Color(0xFF10B981)

private val LightColorScheme = lightColorScheme(
    primary = BluePrimary,
    secondary = TealPrimary,
    tertiary = EmeraldPrimary,
    surface = Color(0xFFFAFAFA),
    background = Color(0xFFFAFAFA),
    inverseSurface = Color(0xFF1F2937),
)

private val DarkColorScheme = darkColorScheme(
    primary = BluePrimary,
    secondary = TealPrimary,
    tertiary = EmeraldPrimary,
    surface = Color(0xFF111827),
    background = Color(0xFF111827),
    inverseSurface = Color(0xFFFAFAFA),
)

@Composable
fun KasipemTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
