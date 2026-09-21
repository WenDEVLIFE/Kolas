package com.wendev.kolas.ui.scan

/** State of the Scan tab. The camera itself lands in Slice 2. */
sealed interface ScanUiState {

    /** Slice 1 scaffold: no camera pipeline exists yet. Deleted once the real states land. */
    data object Placeholder : ScanUiState

    data object RequestingPermission : ScanUiState

    data object PermissionDenied : ScanUiState

    data object CameraReady : ScanUiState

    data object Capturing : ScanUiState

    data class Error(val message: String) : ScanUiState
}
