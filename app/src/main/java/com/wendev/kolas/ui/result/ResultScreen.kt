package com.wendev.kolas.ui.result

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wendev.kolas.R
import com.wendev.kolas.data.detection.Detection
import com.wendev.kolas.ui.components.BackScaffold
import com.wendev.kolas.ui.components.PlaceholderContent
import com.wendev.kolas.ui.theme.KolasTheme
import com.wendev.kolas.ui.theme.emotionColors

@Composable
fun ResultScreen(
    state: ResultUiState,
    onBack: () -> Unit,
    onScanAgain: () -> Unit,
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
            ResultUiState.Loading -> PlaceholderContent(
                title = stringResource(R.string.result_title),
                body = stringResource(R.string.result_loading),
                showProgress = true
            )

            is ResultUiState.Ready -> ResultReadyContent(
                state = state,
                onScanAgain = onScanAgain,
                onOpenChat = onOpenChat
            )

            ResultUiState.LoadingExplanation -> PlaceholderContent(
                title = stringResource(R.string.result_title),
                body = stringResource(R.string.result_loading_explanation),
                showProgress = true
            )

            is ResultUiState.ExplanationFailed -> PlaceholderContent(
                title = stringResource(R.string.result_explanation_failed_title),
                body = state.message,
                actionLabel = stringResource(R.string.result_retry),
                onAction = onRetry
            )

            is ResultUiState.Error -> PlaceholderContent(
                title = stringResource(R.string.result_error_title),
                body = state.message,
                actionLabel = stringResource(R.string.result_retry),
                onAction = onRetry
            )
        }
    }
}

@Composable
private fun ResultReadyContent(
    state: ResultUiState.Ready,
    onScanAgain: () -> Unit,
    onOpenChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        DetectionPhoto(bitmap = state.bitmap.asImageBitmap())
        EmotionSummary(
            emotion = state.detection.emotion,
            confidence = state.detection.confidence
        )
        ScoreBreakdown(allScores = state.detection.allScores, topEmotion = state.detection.emotion)
        ResultActions(onScanAgain = onScanAgain, onOpenChat = onOpenChat)
    }
}

@Composable
private fun DetectionPhoto(bitmap: ImageBitmap, modifier: Modifier = Modifier) {
    Image(
        bitmap = bitmap,
        contentDescription = stringResource(R.string.result_photo_content_description),
        contentScale = ContentScale.Crop,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(MaterialTheme.shapes.medium)
    )
}

@Composable
private fun EmotionSummary(
    emotion: String,
    confidence: Float,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.emotionColors.forEmotion(emotion)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = emotion.take(1).uppercase(),
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = emotion.uppercase(),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stringResource(
                    R.string.result_confidence,
                    (confidence * 100).toInt()
                ),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ScoreBreakdown(
    allScores: Map<String, Float>,
    topEmotion: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.result_scores_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            allScores.forEach { (emotion, score) ->
                ScoreRow(
                    emotion = emotion,
                    score = score,
                    isTop = emotion == topEmotion
                )
            }
        }
    }
}

@Composable
private fun ScoreRow(
    emotion: String,
    score: Float,
    isTop: Boolean,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = emotion.uppercase(),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "${(score * 100).toInt()}%",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { score.coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(MaterialTheme.shapes.small),
            color = MaterialTheme.emotionColors.forEmotion(emotion),
            trackColor = MaterialTheme.colorScheme.surface
        )
    }
}

@Composable
private fun ResultActions(
    onScanAgain: () -> Unit,
    onOpenChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = onOpenChat,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
        ) {
            Text(text = stringResource(R.string.result_open_chat))
        }
        Button(
            onClick = onScanAgain,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
        ) {
            Text(text = stringResource(R.string.result_scan_again))
        }
    }
}

@Preview(name = "Result - Loading", showBackground = true)
@Composable
private fun ResultLoadingPreview() {
    KolasTheme {
        ResultScreen(
            state = ResultUiState.Loading,
            onBack = {},
            onScanAgain = {},
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
            state = ResultUiState.Ready(
                detection = PREVIEW_DETECTION,
                bitmap = previewBitmap()
            ),
            onBack = {},
            onScanAgain = {},
            onRetry = {},
            onOpenChat = {}
        )
    }
}

@Preview(name = "Result - Error", showBackground = true)
@Composable
private fun ResultErrorPreview() {
    KolasTheme {
        ResultScreen(
            state = ResultUiState.Error(message = stringResource(R.string.result_not_found)),
            onBack = {},
            onScanAgain = {},
            onRetry = {},
            onOpenChat = {}
        )
    }
}

private val PREVIEW_DETECTION = Detection(
    id = "preview",
    imagePath = "",
    emotion = "happy",
    confidence = 0.87f,
    allScores = mapOf(
        "alert" to 0.03f,
        "angry" to 0.01f,
        "frown" to 0.02f,
        "happy" to 0.87f,
        "relax" to 0.07f
    ),
    createdAt = 0L
)

private fun previewBitmap(): android.graphics.Bitmap =
    android.graphics.Bitmap.createBitmap(1, 1, android.graphics.Bitmap.Config.ARGB_8888)
