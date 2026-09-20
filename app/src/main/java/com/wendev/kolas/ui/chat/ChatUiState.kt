package com.wendev.kolas.ui.chat

/** State of the pushed chat screen for a single detection. */
sealed interface ChatUiState {

    data object NoModel : ChatUiState

    data class Downloading(val progress: Float?) : ChatUiState

    data object Ready : ChatUiState

    data object Generating : ChatUiState
}
