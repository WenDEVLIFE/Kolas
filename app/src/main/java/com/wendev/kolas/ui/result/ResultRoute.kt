package com.wendev.kolas.ui.result

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ResultRoute(
    detectionId: String,
    onBack: () -> Unit,
    onOpenChat: (String) -> Unit,
    viewModel: ResultViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ResultScreen(
        detectionId = detectionId,
        state = state,
        onBack = onBack,
        onRetry = viewModel::retry,
        onOpenChat = { onOpenChat(detectionId) }
    )
}
