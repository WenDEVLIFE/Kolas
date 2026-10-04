package com.wendev.kolas.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.wendev.kolas.BuildConfig

@Composable
fun AboutRoute(onBack: () -> Unit, modifier: Modifier = Modifier) {
    AboutScreen(
        appVersion = BuildConfig.VERSION_NAME,
        onBack = onBack,
        modifier = modifier
    )
}
