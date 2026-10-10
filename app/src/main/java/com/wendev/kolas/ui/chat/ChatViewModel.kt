package com.wendev.kolas.ui.chat

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wendev.kolas.R
import com.wendev.kolas.data.detection.Detection
import com.wendev.kolas.data.detection.DetectionRepository
import com.wendev.kolas.data.llm.ChatAuthor
import com.wendev.kolas.data.llm.ChatMessage
import com.wendev.kolas.data.llm.ChatPrompt
import com.wendev.kolas.data.llm.LlamaInference
import com.wendev.kolas.data.llm.LlmModelManager
import com.wendev.kolas.data.llm.LlmModelStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Drives the chat: model-download gating (from [LlmModelManager]) plus the
 * conversation. The reply is generated through [LlamaInference] and streamed
 * into the UI. Guardrails live in [ChatPrompt].
 */
@HiltViewModel
class ChatViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    @param:ApplicationContext private val context: Context,
    private val detectionRepository: DetectionRepository,
    private val llmModelManager: LlmModelManager,
    private val llamaInference: LlamaInference
) : ViewModel() {

    private val detectionId: String = savedStateHandle.get<String>(DETECTION_ID_ARG).orEmpty()

    private val messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    private val input = MutableStateFlow("")
    private val isGenerating = MutableStateFlow(false)

    @Volatile
    private var reading: Detection? = null

    private var generationJob: Job? = null

    private val gate: StateFlow<Gate> = llmModelManager.models
        .map { models -> models.firstOrNull { it.isSelected } ?: models.firstOrNull() }
        .map { selected -> selected?.status.toGate() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, Gate.NoModel)

    val state: StateFlow<ChatUiState> =
        combine(gate, messages, input, isGenerating) { g, m, i, generating ->
            when (g) {
                Gate.NoModel -> ChatUiState.NoModel
                is Gate.Downloading -> ChatUiState.Downloading(g.progress)
                is Gate.Failed -> ChatUiState.Error(g.message)
                Gate.Ready -> ChatUiState.Ready(
                    messages = m,
                    input = i,
                    isGenerating = generating
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
            initialValue = ChatUiState.NoModel
        )

    init {
        viewModelScope.launch { reading = detectionRepository.get(detectionId) }

        viewModelScope.launch {
            llmModelManager.models.collect { models ->
                val selected = models.firstOrNull { it.isSelected } ?: models.firstOrNull()
                if (selected?.status == LlmModelStatus.Downloaded) {
                    llamaInference.load(llmModelManager.fileFor(selected.spec))
                }
            }
        }
    }

    fun onDownloadClick() = llmModelManager.download(llmModelManager.selectedModel().id)

    fun onCancelDownload() = llmModelManager.cancel(llmModelManager.selectedModel().id)

    fun onInputChange(value: String) {
        input.value = value
    }

    fun onSend() {
        val text = input.value.trim()
        if (text.isEmpty() || isGenerating.value) return

        val userMessage = ChatMessage(ChatAuthor.USER, text)
        val history = messages.value + userMessage
        messages.value = history + ChatMessage(ChatAuthor.KOLAS, "")
        input.value = ""
        isGenerating.value = true

        generationJob = viewModelScope.launch {
            val prompt = ChatPrompt.buildMessages(
                emotion = reading?.emotion.orEmpty(),
                confidence = reading?.confidence ?: 0f,
                history = history
            )
            val builder = StringBuilder()
            try {
                llamaInference.generate(prompt).collect { delta ->
                    builder.append(delta)
                    replaceLastAssistant(builder.toString())
                }
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (throwable: Throwable) {
                if (builder.isEmpty()) {
                    replaceLastAssistant(context.getString(R.string.chat_reply_failed))
                }
            } finally {
                isGenerating.value = false
            }
        }
    }

    override fun onCleared() {
        generationJob?.cancel()
        super.onCleared()
    }

    private fun replaceLastAssistant(text: String) {
        messages.update { current ->
            if (current.isEmpty()) current
            else current.dropLast(1) + ChatMessage(ChatAuthor.KOLAS, text)
        }
    }

    private fun LlmModelStatus?.toGate(): Gate = when (this) {
        is LlmModelStatus.Downloading -> Gate.Downloading(progress)
        LlmModelStatus.Downloaded -> Gate.Ready
        is LlmModelStatus.Failed -> Gate.Failed(message)
        LlmModelStatus.NotDownloaded, null -> Gate.NoModel
    }

    private sealed interface Gate {
        data object NoModel : Gate
        data class Downloading(val progress: Float?) : Gate
        data class Failed(val message: String) : Gate
        data object Ready : Gate
    }

    private companion object {
        const val DETECTION_ID_ARG = "detectionId"
        const val STOP_TIMEOUT_MS = 5_000L
    }
}
