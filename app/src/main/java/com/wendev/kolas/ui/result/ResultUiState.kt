package com.wendev.kolas.ui.result

/** State of the pushed detection-result screen. */
sealed interface ResultUiState {

    /** Slice 1 scaffold: no detection is loaded or explained yet. Deleted once the real states land. */
    data object Placeholder : ResultUiState

    data object LoadingExplanation : ResultUiState

    data class Ready(
        val emotion: String,
        val explanation: String
    ) : ResultUiState

    data class ExplanationFailed(val message: String) : ResultUiState
}
