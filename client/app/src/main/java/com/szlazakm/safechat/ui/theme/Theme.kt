package com.szlazakm.safechat.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
        primary = Color(0xFFBB86FC), // A softer version of Purple200
        primaryVariant = Color(0xFF3700B3), // Keep this darker variant
        secondary = Color(0xFF03DAC6), // Teal200 works well for accents

        background = Color(0xFF121212), // A typical dark mode background
        surface = Color(0xFF1E1E1E), // Slightly lighter for surfaces like cards
        onPrimary = Color.White, // Text/icons on primary color
        onSecondary = Color.White, // Text/icons on secondary color
        onBackground = Color.White, // Text on the dark background
        onSurface = Color.White // Text on the dark surface
)

private val LightColorPalette = lightColors(
        primary = Purple500,
        primaryVariant = Purple700,
        secondary = Teal200
)

@Composable
fun SafeChatTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
            colors = colors,
            typography = Typography,
            shapes = Shapes,
            content = content
    )
}