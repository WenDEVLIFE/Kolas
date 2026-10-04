package com.wendev.kolas.ui.settings

import com.wendev.kolas.data.preferences.ThemeMode

/** State of the Theme sub-view. */
sealed interface ThemeUiState {

    data object Loading : ThemeUiState

    data class Content(val themeMode: ThemeMode) : ThemeUiState
}
