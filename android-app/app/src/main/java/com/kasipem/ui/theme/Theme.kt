package com.kasipem.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Tema kalem selaras web: latar terang stone, aksen teal tua (#0F766E),
 * slate sebagai warna sekunder. Tanpa warna mencolok / gradien terang.
 */
private val TealDark = Color(0xFF0F766E)
private val Slate = Color(0xFF475569)
private val StoneBackground = Color(0xFFFAFAF9)
private val StoneSurface = Color(0xFFFFFFFF)
private val StoneOnSurface = Color(0xFF1C1917)
private val StoneOnSurfaceVariant = Color(0xFF57534E)

private val LightColorScheme = lightColorScheme(
    primary = TealDark,
    onPrimary = Color.White,
    secondary = Slate,
    onSecondary = Color.White,
    tertiary = Color(0xFF78716C),
    surface = StoneSurface,
    onSurface = StoneOnSurface,
    surfaceVariant = Color(0xFFF5F5F4),
    onSurfaceVariant = StoneOnSurfaceVariant,
    background = StoneBackground,
    onBackground = StoneOnSurface,
    outlineVariant = Color(0xFFE7E5E4),
    error = Color(0xFFB3261E),
    inverseSurface = Color(0xFF292524),
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF2DD4BF),
    onPrimary = Color(0xFF042F2E),
    secondary = Color(0xFF94A3B8),
    onSecondary = Color(0xFF0F172A),
    tertiary = Color(0xFFA8A29E),
    surface = Color(0xFF292524),
    onSurface = Color(0xFFFAFAF9),
    surfaceVariant = Color(0xFF44403C),
    onSurfaceVariant = Color(0xFFD6D3D1),
    background = Color(0xFF1C1917),
    onBackground = Color(0xFFFAFAF9),
    outlineVariant = Color(0xFF57534E),
    error = Color(0xFFF2B8B5),
    inverseSurface = Color(0xFFFAFAF9),
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
