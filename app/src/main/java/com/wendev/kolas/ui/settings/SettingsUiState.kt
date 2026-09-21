package com.wendev.kolas.ui.settings

import com.wendev.kolas.data.preferences.ThemeMode

/** State of the pushed Settings screen. */
sealed interface SettingsUiState {

    data object Loading : SettingsUiState

    data class Content(
        val themeMode: ThemeMode,
        val modelInstalled: Boolean,
        val modelVersion: String?
    ) : SettingsUiState

    data class Downloading(val progress: Float?) : SettingsUiState
}
