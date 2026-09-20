package com.wendev.kolas.ui.result

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Slice 1 skeleton. The detection is read from storage and explained by the model
 * in a later slice; here the state stays in [ResultUiState.LoadingExplanation].
 */
@HiltViewModel
class ResultViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow<ResultUiState>(ResultUiState.LoadingExplanation)
    val state: StateFlow<ResultUiState> = _state.asStateFlow()

    fun retry() {
        // TODO(slice-2): regenerate the explanation for the detection.
        _state.value = ResultUiState.LoadingExplanation
    }
}
