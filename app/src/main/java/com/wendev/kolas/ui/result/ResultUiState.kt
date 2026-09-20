package com.wendev.kolas.ui.result

/** State of the pushed detection-result screen. */
sealed interface ResultUiState {

    data object LoadingExplanation : ResultUiState

    data class Ready(
        val emotion: String,
        val explanation: String
    ) : ResultUiState

    data class ExplanationFailed(val message: String) : ResultUiState
}
