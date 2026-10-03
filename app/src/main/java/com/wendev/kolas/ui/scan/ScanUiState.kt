package com.wendev.kolas.ui.scan

/** State of the Scan tab. */
sealed interface ScanUiState {

    /** Waiting for the runtime camera permission result. Transient and real. */
    data object RequestingPermission : ScanUiState

    /**
     * Camera access was refused. [permanentlyDenied] is true when the OS will no
     * longer show the dialog, so the UI must deep-link to app settings instead of
     * asking again.
     */
    data class PermissionDenied(val permanentlyDenied: Boolean) : ScanUiState

    /** Permission granted; the live preview is bound and the shutter is enabled. */
    data object CameraReady : ScanUiState

    /** A frame was requested; classification and storage are in flight. */
    data object Capturing : ScanUiState

    /** Capture or inference failed. The Scan screen stays up so the user can retry. */
    data class Error(val message: String) : ScanUiState
}
