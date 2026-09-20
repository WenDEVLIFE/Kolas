package com.wendev.kolas.ui.history

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Slice 1 skeleton. No persistence exists yet, so the state starts [HistoryUiState.Empty].
 * Slice 2 swaps this for a repository-backed list.
 */
@HiltViewModel
class HistoryViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow<HistoryUiState>(HistoryUiState.Empty)
    val state: StateFlow<HistoryUiState> = _state.asStateFlow()

    fun retry() {
        // TODO(slice-2): reload detections from the repository.
        _state.value = HistoryUiState.Empty
    }
}
