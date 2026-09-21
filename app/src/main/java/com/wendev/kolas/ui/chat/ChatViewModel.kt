package com.wendev.kolas.ui.chat

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Slice 1 skeleton. No on-device LLM is wired yet, so the state starts at
 * [ChatUiState.NoModel]; the download/generate flow arrives with the AI slice.
 */
@HiltViewModel
class ChatViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow<ChatUiState>(ChatUiState.NoModel)
    val state: StateFlow<ChatUiState> = _state.asStateFlow()
}
