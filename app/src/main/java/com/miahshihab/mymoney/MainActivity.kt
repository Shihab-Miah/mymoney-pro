package com.miahshihab.mymoney

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import com.miahshihab.mymoney.ui.screens.DashboardScreen
import com.miahshihab.mymoney.ui.screens.LoansScreen
import com.miahshihab.mymoney.ui.screens.SettingsScreen
import com.miahshihab.mymoney.ui.theme.AppThemeColors
import com.miahshihab.mymoney.ui.theme.ThemePresets

enum class AppNavScreen {
    DASHBOARD, LOANS, SETTINGS
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val systemDark = isSystemInDarkTheme()
            var currentTheme by remember { mutableStateOf<AppThemeColors>(ThemePresets.Emerald) }
            var isDarkMode by remember { mutableStateOf(systemDark) }
            var isAmoled by remember { mutableStateOf(false) }
            var currentScreen by remember { mutableStateOf(AppNavScreen.DASHBOARD) }

            val colorScheme = if (isDarkMode) {
                ThemePresets.createDarkColorScheme(currentTheme, isAmoled)
            } else {
                ThemePresets.createLightColorScheme(currentTheme)
            }

            MaterialTheme(colorScheme = colorScheme) {
                Surface {
                    when (currentScreen) {
                        AppNavScreen.DASHBOARD -> DashboardScreen(
                            onNavigateToLoans = { currentScreen = AppNavScreen.LOANS },
                            onNavigateToSettings = { currentScreen = AppNavScreen.SETTINGS }
                        )
                        AppNavScreen.LOANS -> LoansScreen(
                            onNavigateBack = { currentScreen = AppNavScreen.DASHBOARD }
                        )
                        AppNavScreen.SETTINGS -> SettingsScreen(
                            currentTheme = currentTheme,
                            onThemeSelected = { currentTheme = it },
                            isDarkMode = isDarkMode,
                            onDarkModeToggled = { isDarkMode = it },
                            isAmoled = isAmoled,
                            onAmoledToggled = { isAmoled = it },
                            onNavigateBack = { currentScreen = AppNavScreen.DASHBOARD }
                        )
                    }
                }
            }
        }
    }
}
