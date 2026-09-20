package com.wendev.kolas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.wendev.kolas.ui.home.HomeScreen
import com.wendev.kolas.ui.splash.SplashRoute
import com.wendev.kolas.ui.theme.KolasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KolasTheme {
                KolasApp()
            }
        }
    }
}

@Composable
private fun KolasApp() {
    var splashFinished by rememberSaveable { mutableStateOf(false) }

    Crossfade(targetState = splashFinished, label = "splash-crossfade") { finished ->
        if (finished) {
            HomeScreen(modifier = Modifier.fillMaxSize())
        } else {
            SplashRoute(onFinished = { splashFinished = true })
        }
    }
}
