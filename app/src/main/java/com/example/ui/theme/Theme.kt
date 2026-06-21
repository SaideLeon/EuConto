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

private val AcademicDarkColorScheme = darkColorScheme(
    primary = AmberBronze,
    onPrimary = EspressoBlack,
    secondary = CreamBronze,
    onSecondary = EspressoBlack,
    tertiary = TerracottaPastel,
    background = EspressoBlack,
    onBackground = SoftClay,
    surface = CocoaDark,
    onSurface = SoftClay,
    surfaceVariant = LatteSlate,
    onSurfaceVariant = SoftClay,
    outline = ClayDivider
)

private val AcademicLightColorScheme = lightColorScheme(
    primary = EspressoBlack,
    onPrimary = Color.White,
    secondary = LatteSlate,
    onSecondary = Color.White,
    tertiary = AmberBronze,
    background = Color(0xFFFAF6F4),
    onBackground = EspressoBlack,
    surface = Color(0xFFF0E6E1),
    onSurface = EspressoBlack,
    surfaceVariant = Color(0xFFE8DAD3),
    onSurfaceVariant = EspressoBlack,
    outline = Color(0xFFD0BEB8)
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) AcademicDarkColorScheme else AcademicLightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    shapes = AppShapes,
    content = content
  )
}
