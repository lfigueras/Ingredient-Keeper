package com.lovely.bakingrecipes.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColors = lightColorScheme(
    primary = Color(0xFFC8956C),            // Caramel
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFF2DFC8),   // Cream
    onPrimaryContainer = Color(0xFF3B2A1A), // Espresso
    secondary = Color(0xFF9C7B5E),          // Mocha
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFF2DFC8),
    onSecondaryContainer = Color(0xFF3B2A1A),
    tertiary = Color(0xFFE8A59B),           // Dusty Rose
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFF7D9D3),
    onTertiaryContainer = Color(0xFF3B2A1A),
    background = Color(0xFFFDF6EE),          // Warm White
    onBackground = Color(0xFF3B2A1A),
    surface = Color(0xFFFFFFFF),            // Card White
    onSurface = Color(0xFF3B2A1A),
    surfaceVariant = Color(0xFFF2DFC8),
    onSurfaceVariant = Color(0xFF9C7B5E),
    outline = Color(0xFF9C7B5E),
    error = Color(0xFFD45C43),              // Terracotta Red
    onError = Color(0xFFFFFFFF)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFD9A97F),
    onPrimary = Color(0xFF3B2A1A),
    primaryContainer = Color(0xFF5A4230),
    onPrimaryContainer = Color(0xFFF2DFC8),
    secondary = Color(0xFFC7A688),
    onSecondary = Color(0xFF3B2A1A),
    secondaryContainer = Color(0xFF4A3826),
    onSecondaryContainer = Color(0xFFF2DFC8),
    tertiary = Color(0xFFE8A59B),
    onTertiary = Color(0xFF3B2A1A),
    tertiaryContainer = Color(0xFF6B4A44),
    onTertiaryContainer = Color(0xFFF7D9D3),
    background = Color(0xFF1E150E),
    onBackground = Color(0xFFF2DFC8),
    surface = Color(0xFF2A1F16),
    onSurface = Color(0xFFF2DFC8),
    surfaceVariant = Color(0xFF4A3826),
    onSurfaceVariant = Color(0xFFD9C3AC),
    outline = Color(0xFF9C7B5E),
    error = Color(0xFFE8A59B),
    onError = Color(0xFF3B2A1A)
)

@Composable
fun BakingRecipesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Brand palette takes priority; only use dynamic color if explicitly enabled.
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }

    // Match the status bar to the app and use dark icons on the light theme so it stays legible.
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primaryContainer.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
