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

private val DarkColorScheme =
  darkColorScheme(
    primary = FriendlyBlue,
    secondary = SunOrange,
    tertiary = WarmYellow,
    background = Color(0xFF121824),
    surface = Color(0xFF1E2638),
    onPrimary = Color(0xFF121824),
    onSecondary = TextLight,
    onTertiary = TextLight,
    onBackground = TextLight,
    onSurface = TextLight
  )

private val LightColorScheme =
  lightColorScheme(
    primary = Color(0xFF4A90E2),
    secondary = SunOrange,
    tertiary = WarmYellow,
    background = SoftBackground,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = TextDark,
    onTertiary = TextDark,
    onBackground = TextDark,
    onSurface = TextDark
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Disable dynamic color to maintain our custom, branded, child-friendly color scheme!
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
