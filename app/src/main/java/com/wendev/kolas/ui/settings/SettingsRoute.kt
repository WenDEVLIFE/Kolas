package com.wendev.kolas.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SettingsRoute(
    onBack: () -> Unit,
    onOpenTheme: () -> Unit,
    onOpenModels: () -> Unit,
    onOpenAbout: () -> Unit,
    onOpenTerms: () -> Unit,
    modifier: Modifier = Modifier
) {
    SettingsScreen(
        onBack = onBack,
        onOpenTheme = onOpenTheme,
        onOpenModels = onOpenModels,
        onOpenAbout = onOpenAbout,
        onOpenTerms = onOpenTerms,
        modifier = modifier
    )
}
