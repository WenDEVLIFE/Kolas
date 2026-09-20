package com.wendev.kolas.ui.result

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.wendev.kolas.R
import com.wendev.kolas.ui.components.BackScaffold
import com.wendev.kolas.ui.components.PlaceholderContent
import com.wendev.kolas.ui.theme.KolasTheme

@Composable
fun ResultScreen(
    detectionId: String,
    state: ResultUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onOpenChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackScaffold(
        title = stringResource(R.string.result_title),
        onBack = onBack,
        modifier = modifier
    ) {
        when (state) {
            ResultUiState.LoadingExplanation -> PlaceholderContent(
                title = stringResource(R.string.result_detection_id, detectionId),
                body = stringResource(R.string.result_loading_explanation),
                showProgress = true
            )

            is ResultUiState.Ready -> PlaceholderContent(
                title = stringResource(R.string.result_detection_id, detectionId),
                body = state.explanation,
                actionLabel = stringResource(R.string.result_open_chat),
                onAction = onOpenChat
            )

            is ResultUiState.ExplanationFailed -> PlaceholderContent(
                title = stringResource(R.string.result_explanation_failed_title),
                body = state.message,
                actionLabel = stringResource(R.string.result_retry),
                onAction = onRetry
            )
        }
    }
}

@Preview(name = "Result - Loading", showBackground = true)
@Composable
private fun ResultLoadingPreview() {
    KolasTheme {
        ResultScreen(
            detectionId = "det-1",
            state = ResultUiState.LoadingExplanation,
            onBack = {},
            onRetry = {},
            onOpenChat = {}
        )
    }
}

@Preview(name = "Result - Ready", showBackground = true)
@Composable
private fun ResultReadyPreview() {
    KolasTheme {
        ResultScreen(
            detectionId = "det-1",
            state = ResultUiState.Ready(
                emotion = "happy",
                explanation = "Your dog looks relaxed and content."
            ),
            onBack = {},
            onRetry = {},
            onOpenChat = {}
        )
    }
}

@Preview(name = "Result - Explanation failed", showBackground = true)
@Composable
private fun ResultFailedPreview() {
    KolasTheme {
        ResultScreen(
            detectionId = "det-1",
            state = ResultUiState.ExplanationFailed(
                message = stringResource(R.string.result_explanation_failed_generic)
            ),
            onBack = {},
            onRetry = {},
            onOpenChat = {}
        )
    }
}
