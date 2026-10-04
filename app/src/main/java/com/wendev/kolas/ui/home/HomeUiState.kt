package com.wendev.kolas.ui.home

/** State of the Home dashboard tab. */
sealed interface HomeUiState {

    /** Scan counters shown on the dashboard. */
    data class Content(
        val todayCount: Int,
        val totalCount: Int
    ) : HomeUiState

    data class Error(val message: String) : HomeUiState
}
