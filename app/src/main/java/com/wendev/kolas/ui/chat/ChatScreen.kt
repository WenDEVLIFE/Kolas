package com.wendev.kolas.ui.chat

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.wendev.kolas.R
import com.wendev.kolas.ui.components.BackScaffold
import com.wendev.kolas.ui.components.PlaceholderContent
import com.wendev.kolas.ui.theme.KolasTheme

@Composable
fun ChatScreen(
    detectionId: String,
    state: ChatUiState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackScaffold(
        title = stringResource(R.string.result_detection_id, detectionId),
        onBack = onBack,
        modifier = modifier
    ) {
        when (state) {
            ChatUiState.NoModel -> PlaceholderContent(
                title = stringResource(R.string.chat_no_model_title),
                body = stringResource(R.string.chat_no_model)
            )

            is ChatUiState.Downloading -> PlaceholderContent(
                title = stringResource(R.string.chat_title),
                body = stringResource(R.string.chat_downloading),
                showProgress = true
            )

            ChatUiState.Ready -> PlaceholderContent(
                title = stringResource(R.string.chat_title),
                body = stringResource(R.string.chat_ready)
            )

            ChatUiState.Generating -> PlaceholderContent(
                title = stringResource(R.string.chat_title),
                body = stringResource(R.string.chat_generating),
                showProgress = true
            )
        }
    }
}

@Preview(name = "Chat - No model", showBackground = true)
@Composable
private fun ChatNoModelPreview() {
    KolasTheme {
        ChatScreen(detectionId = "det-1", state = ChatUiState.NoModel, onBack = {})
    }
}

@Preview(name = "Chat - Downloading", showBackground = true)
@Composable
private fun ChatDownloadingPreview() {
    KolasTheme {
        ChatScreen(
            detectionId = "det-1",
            state = ChatUiState.Downloading(progress = 0.4f),
            onBack = {}
        )
    }
}

@Preview(name = "Chat - Ready", showBackground = true)
@Composable
private fun ChatReadyPreview() {
    KolasTheme {
        ChatScreen(detectionId = "det-1", state = ChatUiState.Ready, onBack = {})
    }
}

@Preview(name = "Chat - Generating", showBackground = true)
@Composable
private fun ChatGeneratingPreview() {
    KolasTheme {
        ChatScreen(detectionId = "det-1", state = ChatUiState.Generating, onBack = {})
    }
}
