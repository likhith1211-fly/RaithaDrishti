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
    primary = ForestGreenLight,
    onPrimary = Color(0xFF022613),
    primaryContainer = Color(0xFF0D472B),
    onPrimaryContainer = Color(0xFFD1F2DF),
    secondary = AmberLight,
    onSecondary = Color(0xFF3A2500),
    secondaryContainer = Color(0xFF5A3C00),
    onSecondaryContainer = Color(0xFFFFECC7),
    tertiary = GhatsTealLight,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    onBackground = Color(0xFFF1F4F0),
    onSurface = Color(0xFFF1F4F0),
    onSurfaceVariant = Color(0xFFC7D4CC),
    outline = DarkBorder
)

private val LightColorScheme = lightColorScheme(
    primary = ForestGreenPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0EFE6),
    onPrimaryContainer = Color(0xFF042A19),
    secondary = AmberSecondary,
    onSecondary = Color.White,
    secondaryContainer = RoyalGoldContainer,
    onSecondaryContainer = Color(0xFF4A3204),
    tertiary = RoyalNavy,
    onTertiary = Color.White,
    background = LightBackground,
    surface = LightSurface,
    surfaceVariant = LightSurfaceVariant,
    onBackground = Color(0xFF141D17),
    onSurface = Color(0xFF141D17),
    onSurfaceVariant = Color(0xFF4C5B52),
    outline = LightBorder
)

@Composable
fun RaithaDrishtiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep consistent agricultural branding
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
