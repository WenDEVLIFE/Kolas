package com.wendev.kolas.ui.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wendev.kolas.R
import com.wendev.kolas.data.llm.ChatAuthor
import com.wendev.kolas.data.llm.ChatMessage
import com.wendev.kolas.ui.components.BackScaffold
import com.wendev.kolas.ui.components.PlaceholderContent
import com.wendev.kolas.ui.theme.KolasTheme
import kotlin.math.roundToInt

@Composable
fun ChatScreen(
    detectionId: String,
    state: ChatUiState,
    onBack: () -> Unit,
    onDownloadClick: () -> Unit,
    onCancelDownload: () -> Unit,
    onRetry: () -> Unit,
    onInputChange: (String) -> Unit,
    onSend: () -> Unit,
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
                body = stringResource(R.string.chat_no_model),
                actionLabel = stringResource(R.string.chat_download_model),
                onAction = onDownloadClick
            )

            is ChatUiState.Downloading -> ChatDownloadingContent(
                progress = state.progress,
                onCancel = onCancelDownload
            )

            ChatUiState.Preparing -> PlaceholderContent(
                title = stringResource(R.string.chat_preparing_title),
                body = stringResource(R.string.chat_preparing),
                showProgress = true
            )

            is ChatUiState.Error -> PlaceholderContent(
                title = stringResource(R.string.chat_error_title),
                body = state.message,
                actionLabel = stringResource(R.string.chat_retry),
                onAction = onRetry
            )

            is ChatUiState.Ready -> ChatConversation(
                state = state,
                onInputChange = onInputChange,
                onSend = onSend
            )
        }
    }
}

@Composable
private fun ChatConversation(
    state: ChatUiState.Ready,
    onInputChange: (String) -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.lastIndex)
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(state.messages) { index, message ->
                val streaming = state.isGenerating &&
                    index == state.messages.lastIndex &&
                    message.author == ChatAuthor.KOLAS
                AnimatedMessageBubble(message = message, streaming = streaming)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = state.input,
                onValueChange = onInputChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text(text = stringResource(R.string.chat_input_hint)) },
                maxLines = 4,
                enabled = !state.isGenerating
            )
            IconButton(
                onClick = onSend,
                enabled = state.input.isNotBlank() && !state.isGenerating
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = stringResource(R.string.chat_send)
                )
            }
        }
    }
}

@Composable
private fun AnimatedMessageBubble(
    message: ChatMessage,
    streaming: Boolean,
    modifier: Modifier = Modifier
) {
    var appeared by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { appeared = true }

    AnimatedVisibility(
        visible = appeared,
        enter = fadeIn(animationSpec = tween(durationMillis = 220)) +
            slideInVertically(animationSpec = tween(durationMillis = 220)) { height -> height / 3 },
        modifier = modifier
    ) {
        MessageBubble(message = message, streaming = streaming)
    }
}

@Composable
private fun MessageBubble(
    message: ChatMessage,
    streaming: Boolean,
    modifier: Modifier = Modifier
) {
    val isUser = message.author == ChatAuthor.USER
    val contentColor = if (isUser) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            color = if (isUser) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            },
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            if (streaming && message.text.isBlank()) {
                TypingIndicator(
                    color = contentColor,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
                )
            } else {
                StreamingText(
                    text = message.text,
                    streaming = streaming,
                    color = contentColor,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }
    }
}

/** Renders the message text; while [streaming], a blinking caret trails the text. */
@Composable
private fun StreamingText(
    text: String,
    streaming: Boolean,
    color: Color,
    modifier: Modifier = Modifier
) {
    if (!streaming) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = color,
            modifier = modifier
        )
        return
    }

    val transition = rememberInfiniteTransition(label = "caret")
    val caretAlpha by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 520, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "caret-alpha"
    )

    Text(
        text = buildAnnotatedString {
            append(text)
            withStyle(SpanStyle(color = color.copy(alpha = caretAlpha))) {
                append("\u258F")
            }
        },
        style = MaterialTheme.typography.bodyMedium,
        color = color,
        modifier = modifier
    )
}

/** Three pulsing dots shown while the model is thinking. */
@Composable
private fun TypingIndicator(
    color: Color,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "typing")
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(TYPING_DOT_COUNT) { index ->
            val alpha by transition.animateFloat(
                initialValue = 0.25f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 480,
                        delayMillis = index * 160,
                        easing = LinearEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "dot-$index"
            )
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = alpha))
            )
        }
    }
}

private const val TYPING_DOT_COUNT = 3

@Composable
private fun ChatDownloadingContent(
    progress: Float?,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.chat_title),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.chat_downloading),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 12.dp)
            )
            if (progress != null) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .widthIn(max = 240.dp)
                        .fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = stringResource(R.string.chat_download_progress, (progress * 100).roundToInt()),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )
            } else {
                LinearProgressIndicator(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .widthIn(max = 240.dp)
                        .fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Button(
                onClick = onCancel,
                modifier = Modifier
                    .padding(top = 24.dp)
                    .heightIn(min = 48.dp)
            ) {
                Text(text = stringResource(R.string.chat_download_cancel))
            }
        }
    }
}

@Preview(name = "Chat - No model", showBackground = true)
@Composable
private fun ChatNoModelPreview() {
    KolasTheme {
        ChatScreen(
            detectionId = "det-1",
            state = ChatUiState.NoModel,
            onBack = {}, onDownloadClick = {}, onCancelDownload = {}, onRetry = {},
            onInputChange = {}, onSend = {}
        )
    }
}

@Preview(name = "Chat - Downloading", showBackground = true)
@Composable
private fun ChatDownloadingPreview() {
    KolasTheme {
        ChatScreen(
            detectionId = "det-1",
            state = ChatUiState.Downloading(progress = 0.4f),
            onBack = {}, onDownloadClick = {}, onCancelDownload = {}, onRetry = {},
            onInputChange = {}, onSend = {}
        )
    }
}

@Preview(name = "Chat - Preparing", showBackground = true)
@Composable
private fun ChatPreparingPreview() {
    KolasTheme {
        ChatScreen(
            detectionId = "det-1",
            state = ChatUiState.Preparing,
            onBack = {}, onDownloadClick = {}, onCancelDownload = {}, onRetry = {},
            onInputChange = {}, onSend = {}
        )
    }
}

@Preview(name = "Chat - Conversation", showBackground = true)
@Composable
private fun ChatConversationPreview() {
    KolasTheme {
        ChatScreen(
            detectionId = "det-1",
            state = ChatUiState.Ready(
                messages = listOf(
                    ChatMessage(ChatAuthor.USER, "Why is my dog alert?"),
                    ChatMessage(ChatAuthor.KOLAS, "An alert reading often means your dog noticed something nearby.")
                ),
                input = "",
                isGenerating = false
            ),
            onBack = {}, onDownloadClick = {}, onCancelDownload = {}, onRetry = {},
            onInputChange = {}, onSend = {}
        )
    }
}

@Preview(name = "Chat - Streaming", showBackground = true)
@Composable
private fun ChatStreamingPreview() {
    KolasTheme {
        ChatScreen(
            detectionId = "det-1",
            state = ChatUiState.Ready(
                messages = listOf(
                    ChatMessage(ChatAuthor.USER, "Why is my dog alert?"),
                    ChatMessage(ChatAuthor.KOLAS, "An alert reading")
                ),
                input = "",
                isGenerating = true
            ),
            onBack = {}, onDownloadClick = {}, onCancelDownload = {}, onRetry = {},
            onInputChange = {}, onSend = {}
        )
    }
}

@Preview(name = "Chat - Error", showBackground = true)
@Composable
private fun ChatErrorPreview() {
    KolasTheme {
        ChatScreen(
            detectionId = "det-1",
            state = ChatUiState.Error(message = stringResource(R.string.chat_download_failed_generic)),
            onBack = {}, onDownloadClick = {}, onCancelDownload = {}, onRetry = {},
            onInputChange = {}, onSend = {}
        )
    }
}
