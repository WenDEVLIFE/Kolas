package com.wendev.kolas.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wendev.kolas.R
import com.wendev.kolas.data.llm.ChatModels
import com.wendev.kolas.data.llm.LlmModelStatus
import com.wendev.kolas.ui.components.BackScaffold
import com.wendev.kolas.ui.components.PlaceholderContent
import com.wendev.kolas.ui.theme.KolasTheme
import kotlin.math.roundToInt

@Composable
fun ModelsScreen(
    state: ModelsUiState,
    onBack: () -> Unit,
    onSelect: (String) -> Unit,
    onDownload: (String) -> Unit,
    onCancel: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BackScaffold(
        title = stringResource(R.string.settings_item_models),
        onBack = onBack,
        modifier = modifier
    ) {
        when (state) {
            ModelsUiState.Loading -> PlaceholderContent(
                title = stringResource(R.string.settings_item_models),
                body = stringResource(R.string.settings_models_loading),
                showProgress = true
            )

            is ModelsUiState.Content -> ModelsContent(
                chatModels = state.chatModels,
                onSelect = onSelect,
                onDownload = onDownload,
                onCancel = onCancel
            )
        }
    }
}

@Composable
private fun ModelsContent(
    chatModels: List<ChatModelRow>,
    onSelect: (String) -> Unit,
    onDownload: (String) -> Unit,
    onCancel: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SectionTitle(stringResource(R.string.settings_builtin_models_title))
        BuiltinModelRow(
            name = stringResource(R.string.settings_model_emotion_name),
            description = stringResource(R.string.settings_model_emotion_description)
        )
        BuiltinModelRow(
            name = stringResource(R.string.settings_model_detector_name),
            description = stringResource(R.string.settings_model_detector_description)
        )

        SectionTitle(
            text = stringResource(R.string.settings_chat_models_title),
            modifier = Modifier.padding(top = 12.dp)
        )
        chatModels.forEach { row ->
            ChatModelPickerRow(
                row = row,
                onSelect = onSelect,
                onDownload = onDownload,
                onCancel = onCancel
            )
        }
    }
}

@Composable
private fun SectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = modifier
    )
}

@Composable
private fun BuiltinModelRow(
    name: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = stringResource(R.string.settings_model_status_ready),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun ChatModelPickerRow(
    row: ChatModelRow,
    onSelect: (String) -> Unit,
    onDownload: (String) -> Unit,
    onCancel: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val status = row.status
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            RadioButton(
                selected = row.isSelected,
                onClick = { onSelect(row.id) }
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = row.name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = row.summary,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            ChatModelAction(
                status = status,
                onDownload = { onDownload(row.id) }
            )
        }

        if (status is LlmModelStatus.Downloading) {
            DownloadProgress(
                progress = status.progress,
                onCancel = { onCancel(row.id) }
            )
        } else if (status is LlmModelStatus.Failed) {
            DownloadError(
                message = status.message,
                onRetry = { onDownload(row.id) }
            )
        }
    }
}

@Composable
private fun ChatModelAction(
    status: LlmModelStatus,
    onDownload: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (status) {
        LlmModelStatus.NotDownloaded -> TextButton(onClick = onDownload, modifier = modifier) {
            Text(text = stringResource(R.string.settings_model_download))
        }

        LlmModelStatus.Downloaded -> Text(
            text = stringResource(R.string.settings_model_status_ready),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = modifier
        )

        // The error message and retry render under the row.
        is LlmModelStatus.Failed -> Unit

        // The progress bar renders under the row while downloading.
        is LlmModelStatus.Downloading -> Unit
    }
}

@Composable
private fun DownloadProgress(
    progress: Float?,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 56.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (progress != null) {
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary
            )
        } else {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (progress != null) {
                    stringResource(
                        R.string.settings_model_download_percent,
                        (progress * 100).roundToInt()
                    )
                } else {
                    stringResource(R.string.settings_model_downloading)
                },
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            TextButton(onClick = onCancel) {
                Text(text = stringResource(R.string.settings_model_cancel))
            }
        }
    }
}

@Composable
private fun DownloadError(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 56.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error
        )
        TextButton(onClick = onRetry) {
            Text(text = stringResource(R.string.settings_model_retry))
        }
    }
}

@Preview(name = "Models", showBackground = true)
@Composable
private fun ModelsPreview() {
    KolasTheme {
        ModelsScreen(
            state = ModelsUiState.Content(
                chatModels = ChatModels.ALL.mapIndexed { index, spec ->
                    ChatModelRow(
                        id = spec.id,
                        name = spec.displayName,
                        summary = "${spec.tier} \u00b7 ${spec.minRamGb} GB RAM",
                        status = when (index) {
                            0 -> LlmModelStatus.Downloading(progress = 0.42f)
                            1 -> LlmModelStatus.Downloaded
                            else -> LlmModelStatus.NotDownloaded
                        },
                        isSelected = index == 1
                    )
                }
            ),
            onBack = {},
            onSelect = {},
            onDownload = {},
            onCancel = {}
        )
    }
}
