package com.wendev.kolas

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wendev.kolas.data.preferences.SettingsPreferences
import com.wendev.kolas.data.preferences.ThemeMode
import com.wendev.kolas.ui.navigation.KolasNavHost
import com.wendev.kolas.ui.theme.KolasTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var settingsPreferences: SettingsPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeMode by settingsPreferences.themeMode
                .collectAsStateWithLifecycle(initialValue = ThemeMode.SYSTEM)

            val darkTheme = when (themeMode) {
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
            }

            // System bar icons do not follow the Compose theme on their own, so
            // re-apply the edge-to-edge styles whenever the resolved mode flips.
            LaunchedEffect(darkTheme) {
                enableEdgeToEdge(
                    statusBarStyle = systemBarStyle(darkTheme),
                    navigationBarStyle = systemBarStyle(darkTheme)
                )
            }

            KolasTheme(themeMode = themeMode) {
                KolasNavHost()
            }
        }
    }
}

private fun systemBarStyle(darkTheme: Boolean): SystemBarStyle = if (darkTheme) {
    SystemBarStyle.dark(Color.TRANSPARENT)
} else {
    SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
}
