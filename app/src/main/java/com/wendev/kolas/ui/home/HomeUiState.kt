package com.wendev.kolas.ui.home

import com.wendev.kolas.ui.history.HistoryItem

/** State of the Home dashboard tab. */
sealed interface HomeUiState {

    /** No scans have been recorded yet — the honest default, since no data source exists. */
    data object Empty : HomeUiState

    data class Content(val recent: List<HistoryItem>) : HomeUiState

    data class Error(val message: String) : HomeUiState
}
