package com.wendev.kolas.ui.scan

/** One-off effects emitted by [ScanViewModel]. Never persisted as state. */
sealed interface ScanEvent {
    data class Detected(val detectionId: String) : ScanEvent
}
