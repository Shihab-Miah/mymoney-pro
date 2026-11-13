package com.miahshihab.mymoney.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

data class AppThemeColors(
    val name: String,
    val primary: Color,
    val secondary: Color,
    val tertiary: Color
)

object ThemePresets {
    val Emerald = AppThemeColors(
        name = "Emerald Green",
        primary = Color(0xFF2E7D32),
        secondary = Color(0xFF00897B),
        tertiary = Color(0xFF81C784)
    )

    val OceanBlue = AppThemeColors(
        name = "Ocean Blue",
        primary = Color(0xFF1565C0),
        secondary = Color(0xFF0288D1),
        tertiary = Color(0xFF64B5F6)
    )

    val SunsetAmber = AppThemeColors(
        name = "Sunset Amber",
        primary = Color(0xFFE65100),
        secondary = Color(0xFFF57C00),
        tertiary = Color(0xFFFFB74D)
    )

    val RoyalPurple = AppThemeColors(
        name = "Royal Purple",
        primary = Color(0xFF6A1B9A),
        secondary = Color(0xFF8E24AA),
        tertiary = Color(0xFFBA68C8)
    )

    val AmoledMidnight = AppThemeColors(
        name = "Midnight Dark",
        primary = Color(0xFF64B5F6),
        secondary = Color(0xFF81C784),
        tertiary = Color(0xFFFFB74D)
    )

    val AllPresets = listOf(Emerald, OceanBlue, SunsetAmber, RoyalPurple, AmoledMidnight)

    fun createLightColorScheme(theme: AppThemeColors) = lightColorScheme(
        primary = theme.primary,
        secondary = theme.secondary,
        tertiary = theme.tertiary,
        background = Color(0xFFF8F9FA),
        surface = Color(0xFFFFFFFF),
        onPrimary = Color.White,
        onSecondary = Color.White,
        onBackground = Color(0xFF1C1B1F),
        onSurface = Color(0xFF1C1B1F)
    )

    fun createDarkColorScheme(theme: AppThemeColors, isAmoled: Boolean = false) = darkColorScheme(
        primary = theme.primary,
        secondary = theme.secondary,
        tertiary = theme.tertiary,
        background = if (isAmoled) Color.Black else Color(0xFF121212),
        surface = if (isAmoled) Color(0xFF0A0A0A) else Color(0xFF1E1E1E),
        onPrimary = Color.White,
        onSecondary = Color.White,
        onBackground = Color(0xFFE0E0E0),
        onSurface = Color(0xFFE0E0E0)
    )
}
