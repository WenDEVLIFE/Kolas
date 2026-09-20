package com.wendev.kolas.ui.settings

/** State of the pushed Settings screen. */
sealed interface SettingsUiState {

    data object Loading : SettingsUiState

    data class Content(
        val modelInstalled: Boolean,
        val modelVersion: String?
    ) : SettingsUiState

    data class Downloading(val progress: Float?) : SettingsUiState
}
