package com.wendev.kolas.ui.settings

import com.wendev.kolas.data.llm.LlmModelStatus

/** One downloadable chat model row in the picker. */
data class ChatModelRow(
    val id: String,
    val name: String,
    val summary: String,
    val status: LlmModelStatus,
    val isSelected: Boolean
)

/** State of the Settings → Models screen. */
sealed interface ModelsUiState {

    data object Loading : ModelsUiState

    data class Content(val chatModels: List<ChatModelRow>) : ModelsUiState
}
