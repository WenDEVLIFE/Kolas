package com.wendev.kolas.ui.scan

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * Slice 1 skeleton. The camera pipeline is not wired yet; the state machine and
 * the event channel are real so Slice 2 only has to fill the body.
 */
@HiltViewModel
class ScanViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow<ScanUiState>(ScanUiState.RequestingPermission)
    val state: StateFlow<ScanUiState> = _state.asStateFlow()

    private val _events = Channel<ScanEvent>(Channel.BUFFERED)
    val events: Flow<ScanEvent> = _events.receiveAsFlow()

    fun onPermissionResult(granted: Boolean) {
        _state.value = if (granted) ScanUiState.CameraReady else ScanUiState.PermissionDenied
    }

    fun startCapture() {
        // TODO(slice-2): request CAMERA, bind CameraX, capture a frame, classify it
        // and emit ScanEvent.Detected(id). Navigation stays out of the ViewModel.
    }

    fun retry() {
        _state.value = ScanUiState.RequestingPermission
    }
}
