package com.wendev.kolas.ui.settings

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Slice 1 skeleton. The model download flow arrives with the AI slice; this only
 * holds the state shape.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {

    // Slice 1 has no download flow yet, so the honest state is "no model installed".
    // The real Content arrives with the AI slice.
    private val _state = MutableStateFlow<SettingsUiState>(
        SettingsUiState.Content(modelInstalled = false, modelVersion = null)
    )
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()
}
