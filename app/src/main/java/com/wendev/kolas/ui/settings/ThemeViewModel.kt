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
import kotlinx.coroutines.launch

/**
 * Theme selection state. The theme is persisted, so the screen starts in
 * [ThemeUiState.Loading] for the brief DataStore read and resolves to
 * [ThemeUiState.Content] as soon as the first value arrives.
 */
@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val settingsPreferences: SettingsPreferences
) : ViewModel() {

    private val _state = MutableStateFlow<ThemeUiState>(ThemeUiState.Loading)
    val state: StateFlow<ThemeUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            settingsPreferences.themeMode.collect { mode ->
                _state.value = ThemeUiState.Content(mode)
            }
        }
    }

    fun onThemeModeSelected(mode: ThemeMode) {
        viewModelScope.launch { settingsPreferences.setThemeMode(mode) }
    }
}
