package com.wendev.kolas.ui.result

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ResultRoute(
    onBack: () -> Unit,
    onScanAgain: () -> Unit,
    onOpenChat: (String) -> Unit,
    viewModel: ResultViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ResultScreen(
        state = state,
        onBack = onBack,
        onScanAgain = onScanAgain,
        onRetry = viewModel::retry,
        onOpenChat = {
            val detection = (state as? ResultUiState.Ready)?.detection
            if (detection != null) onOpenChat(detection.id)
        }
    )
}
