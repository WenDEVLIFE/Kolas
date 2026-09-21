package com.wendev.kolas.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wendev.kolas.data.preferences.SettingsPreferences
import com.wendev.kolas.data.preferences.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Settings state. The theme is persisted, so the screen starts in [SettingsUiState.Loading]
 * for the brief DataStore read and resolves to [SettingsUiState.Content] as soon as the
 * first value arrives (or the default falls back in).
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsPreferences: SettingsPreferences
) : ViewModel() {

    private val _state = MutableStateFlow<SettingsUiState>(SettingsUiState.Loading)
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            settingsPreferences.themeMode.collect { themeMode ->
                _state.update { current ->
                    when (current) {
                        is SettingsUiState.Content -> current.copy(themeMode = themeMode)
                        // Slice 1 has no download flow yet, so the only honest Content
                        // is "no model installed".
                        else -> SettingsUiState.Content(
                            themeMode = themeMode,
                            modelInstalled = false,
                            modelVersion = null
                        )
                    }
                }
            }
        }
    }

    fun onThemeModeSelected(mode: ThemeMode) {
        viewModelScope.launch { settingsPreferences.setThemeMode(mode) }
    }
}
