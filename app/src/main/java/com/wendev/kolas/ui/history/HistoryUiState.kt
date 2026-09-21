package com.wendev.kolas.ui.history

/** A single stored detection shown in the history list. */
data class HistoryItem(
    val id: String,
    val emotion: String,
    val summary: String
)

/** State of the History tab. */
sealed interface HistoryUiState {

    data object Empty : HistoryUiState

    data class Content(val items: List<HistoryItem>) : HistoryUiState

    data class Error(val message: String) : HistoryUiState
}
