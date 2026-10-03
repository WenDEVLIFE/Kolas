package com.wendev.kolas.ui.scan

/** One-off effects emitted by [ScanViewModel]. Never persisted as state. */
sealed interface ScanEvent {

    /**
     * The Route must show the runtime CAMERA permission dialog. The ViewModel asks
     * for the permission but cannot launch the system dialog by itself.
     */
    data object RequestCameraPermission : ScanEvent

    /** A reading was stored; navigate to the Result screen for [detectionId]. */
    data class Detected(val detectionId: String) : ScanEvent

    /** The captured frame was not a dog; the Scan screen shows a blocking dialog. */
    data object NotADog : ScanEvent
}
