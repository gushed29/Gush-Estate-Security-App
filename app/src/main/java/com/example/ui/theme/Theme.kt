package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val FrostedGlassColorScheme = lightColorScheme(
    primary = GushedCobalt,                 // Indigo 600
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEEF2FF),   // Indigo 50
    onPrimaryContainer = Color(0xFF312E81), // Indigo 900
    secondary = GushedEmeraldApproved,      // Emerald 600
    onSecondary = Color.White,
    secondaryContainer = GushedEmeraldDark, // Emerald 100
    onSecondaryContainer = Color(0xFF064E3B),
    tertiary = GushedIndigoStaff,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFE0E7FF),
    onTertiaryContainer = Color(0xFF3730A3),
    background = FrostedBackground,        // #E8EDF2 Cool Fog
    onBackground = GushedTextPrimary,      // #0F172A Slate 900
    surface = FrostedGlassSurfaceElevated, // #F2FFFFFF
    onSurface = GushedTextPrimary,
    surfaceVariant = FrostedGlassSurfaceSubtle,
    onSurfaceVariant = GushedTextSecondary,
    outline = GushedBorder,
    outlineVariant = FrostedGlassBorder,
    error = GushedCrimsonDenied,
    onError = Color.White,
    errorContainer = GushedCrimsonDark,
    onErrorContainer = Color(0xFF991B1B)
)

@Composable
fun GushedSecurityTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = FrostedGlassColorScheme,
        typography = Typography,
        content = content
    )
}
