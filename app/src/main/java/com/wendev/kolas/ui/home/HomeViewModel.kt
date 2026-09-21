package com.wendev.kolas.ui.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * There is no data source for Home yet, so the state starts — and stays —
 * [HomeUiState.Empty]. No async work exists to resolve a spinner, so we never
 * default to a loading state.
 */
@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow<HomeUiState>(HomeUiState.Empty)
    val state: StateFlow<HomeUiState> = _state.asStateFlow()
}
