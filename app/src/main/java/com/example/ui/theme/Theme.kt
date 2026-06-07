package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = SoftSage,
    secondary = SageGreen,
    tertiary = GoldYellow,
    background = CharcoalGray,
    surface = Color(0xFF383E39),
    onPrimary = CharcoalGray,
    onSecondary = Color.White,
    onBackground = CreamBackground,
    onSurface = CreamBackground
)

private val LightColorScheme = lightColorScheme(
    primary = ForestDeep,
    secondary = SageGreen,
    tertiary = GoldYellow,
    background = CreamBackground,
    surface = SoftSandCard,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = CharcoalGray,
    onSurface = CharcoalGray
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Set to false to enforce our bespoke Forest Organic branding
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
