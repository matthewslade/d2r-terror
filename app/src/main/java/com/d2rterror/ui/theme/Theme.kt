package com.d2rterror.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val D2RColorScheme = darkColorScheme(
    primary = D2RGold,
    onPrimary = D2RDark,
    primaryContainer = D2RGoldLight,
    onPrimaryContainer = D2RDarker,

    secondary = D2RRed,
    onSecondary = TextPrimary,
    secondaryContainer = D2RRedDark,
    onSecondaryContainer = TextPrimary,

    tertiary = D2RBlue,
    onTertiary = TextPrimary,

    background = D2RDarker,
    onBackground = TextPrimary,

    surface = D2RSurface,
    onSurface = TextPrimary,
    surfaceVariant = D2RSurfaceVariant,
    onSurfaceVariant = TextSecondary,

    error = D2RRed,
    onError = TextPrimary,

    outline = TextMuted
)

@Composable
fun D2RTerrorTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = D2RColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = D2RDarker.toArgb()
            window.navigationBarColor = D2RDarker.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
