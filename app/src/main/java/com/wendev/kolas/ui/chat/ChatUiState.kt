package com.wendev.kolas.ui.chat

import com.wendev.kolas.data.llm.ChatMessage

/** State of the pushed chat screen for a single detection. */
sealed interface ChatUiState {

    data object NoModel : ChatUiState

    data class Downloading(val progress: Float?) : ChatUiState

    data class Error(val message: String) : ChatUiState

    data class Ready(
        val messages: List<ChatMessage>,
        val input: String,
        val isGenerating: Boolean
    ) : ChatUiState
}
