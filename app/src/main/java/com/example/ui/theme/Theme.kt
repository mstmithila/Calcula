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

// 1. Classic Themes
private val ClassicDark = darkColorScheme(
    primary = ClassicOrange,
    secondary = ClassicBlue,
    tertiary = ClassicOperatorDark,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    surfaceVariant = Color(0xFF2E2E2E),
    onSurfaceVariant = Color(0xFFCCCCCC)
)

private val ClassicLight = lightColorScheme(
    primary = ClassicOrange,
    secondary = ClassicBlue,
    tertiary = Color(0xFF00ACC1),
    background = LightBackground,
    surface = LightSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFE5E5E5),
    onSurfaceVariant = Color(0xFF555555)
)

// 2. Teal Themes
private val TealDark = darkColorScheme(
    primary = TealAccent,
    secondary = TealPrimary,
    tertiary = Color(0xFF00E5FF),
    background = TealDarkGrey,
    surface = Color(0xFF223531),
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    surfaceVariant = Color(0xFF2B413D),
    onSurfaceVariant = Color(0xFFAEBDBA)
)

private val TealLight = lightColorScheme(
    primary = TealPrimary,
    secondary = TealAccent,
    tertiary = Color(0xFF00838F),
    background = Color(0xFFF0F5F4),
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color(0xFF112220),
    onSurface = Color(0xFF112220),
    surfaceVariant = Color(0xFFE2ECEB),
    onSurfaceVariant = Color(0xFF4A5C59)
)

// 3. Solar Orange Themes
private val SolarDark = darkColorScheme(
    primary = SolarAccent,
    secondary = SolarPrimary,
    tertiary = Color(0xFFFFAB40),
    background = SolarDarkGrey,
    surface = Color(0xFF38261E),
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    surfaceVariant = Color(0xFF422E25),
    onSurfaceVariant = Color(0xFFE1D1CA)
)

private val SolarLight = lightColorScheme(
    primary = SolarPrimary,
    secondary = SolarAccent,
    tertiary = Color(0xFFE65100),
    background = Color(0xFFFAF6F4),
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color(0xFF21140E),
    onSurface = Color(0xFF21140E),
    surfaceVariant = Color(0xFFECE1DB),
    onSurfaceVariant = Color(0xFF5D4E47)
)

// 4. Purple Themes
private val PurpleDark = darkColorScheme(
    primary = PurpleAccent,
    secondary = PurplePrimary,
    tertiary = Color(0xFFD500F9),
    background = PurpleDarkGrey,
    surface = Color(0xFF31223F),
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    surfaceVariant = Color(0xFF3B2A4C),
    onSurfaceVariant = Color(0xFFD6C8E3)
)

private val PurpleLight = lightColorScheme(
    primary = PurplePrimary,
    secondary = PurpleAccent,
    tertiary = Color(0xFF6A1B9A),
    background = Color(0xFFFAF5FC),
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color(0xFF221133),
    onSurface = Color(0xFF221133),
    surfaceVariant = Color(0xFFECE1F2),
    onSurfaceVariant = Color(0xFF5A496B)
)

@Composable
fun MyApplicationTheme(
    themeName: String = "classic",
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        else -> {
            when (themeName) {
                "teal" -> if (darkTheme) TealDark else TealLight
                "orange" -> if (darkTheme) SolarDark else SolarLight
                "purple" -> if (darkTheme) PurpleDark else PurpleLight
                else -> if (darkTheme) ClassicDark else ClassicLight
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
