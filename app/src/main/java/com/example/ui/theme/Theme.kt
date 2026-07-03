package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
  darkColorScheme(
    primary = EmeraldPrimaryDark,
    onPrimary = EmeraldOnPrimaryDark,
    secondary = EmeraldSecondaryDark,
    background = EmeraldBackgroundDark,
    surface = EmeraldSurfaceDark,
    onBackground = EmeraldOnBackgroundDark,
    onSurface = EmeraldOnSurfaceDark
  )

private val LightColorScheme =
  lightColorScheme(
    primary = VibrantGreen,
    onPrimary = Color.White,
    secondary = VibrantGreenDark,
    background = VibrantBgLight,
    surface = VibrantSurfaceLight,
    onBackground = VibrantTextPrimaryLight,
    onSurface = VibrantTextPrimaryLight
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Override dynamicColor default to false so that the custom branded emerald theme is visible
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
