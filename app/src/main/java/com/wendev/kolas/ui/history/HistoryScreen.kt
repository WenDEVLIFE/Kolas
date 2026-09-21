package com.wendev.kolas.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wendev.kolas.R
import com.wendev.kolas.ui.components.PlaceholderContent
import com.wendev.kolas.ui.theme.KolasTheme
import com.wendev.kolas.ui.theme.emotionColors

@Composable
fun HistoryScreen(
    state: HistoryUiState,
    onOpenResult: (String) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (state) {
        HistoryUiState.Empty -> PlaceholderContent(
            title = stringResource(R.string.history_empty_title),
            body = stringResource(R.string.history_empty_body),
            modifier = modifier
        )

        is HistoryUiState.Content -> Surface(
            modifier = modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(items = state.items, key = { it.id }) { item ->
                    HistoryRow(item = item, onClick = { onOpenResult(item.id) })
                }
            }
        }

        is HistoryUiState.Error -> PlaceholderContent(
            title = stringResource(R.string.history_error_title),
            body = state.message,
            actionLabel = stringResource(R.string.history_retry),
            onAction = onRetry,
            modifier = modifier
        )
    }
}

@Composable
private fun HistoryRow(
    item: HistoryItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.emotionColors.forEmotion(item.emotion))
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.emotion.uppercase(),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = item.summary,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Preview(name = "History - Empty", showBackground = true)
@Composable
private fun HistoryEmptyPreview() {
    KolasTheme {
        HistoryScreen(state = HistoryUiState.Empty, onOpenResult = {}, onRetry = {})
    }
}

@Preview(name = "History - Content", showBackground = true)
@Composable
private fun HistoryContentPreview() {
    KolasTheme {
        HistoryScreen(
            state = HistoryUiState.Content(
                items = listOf(
                    HistoryItem(id = "1", emotion = "happy", summary = "Tail up, ears relaxed."),
                    HistoryItem(id = "2", emotion = "alert", summary = "Head tilted, ears forward.")
                )
            ),
            onOpenResult = {},
            onRetry = {}
        )
    }
}

@Preview(name = "History - Error", showBackground = true)
@Composable
private fun HistoryErrorPreview() {
    KolasTheme {
        HistoryScreen(
            state = HistoryUiState.Error(message = stringResource(R.string.history_error_generic)),
            onOpenResult = {},
            onRetry = {}
        )
    }
}
