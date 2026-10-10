package com.wendev.kolas.data.llm

import java.io.File
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/** Lifecycle of the on-device engine. */
sealed interface LlmEngineStatus {
    data object Idle : LlmEngineStatus
    data object Loading : LlmEngineStatus
    data object Ready : LlmEngineStatus
    data class Error(val message: String) : LlmEngineStatus
}

/**
 * Boundary to the on-device LLM engine. The llama.cpp JNI implementation lives
 * behind this interface.
 */
interface LlamaInference {

    /** Current engine lifecycle state. */
    val status: StateFlow<LlmEngineStatus>

    /** Loads [modelFile]; safe to call repeatedly (no-op if already loaded). */
    suspend fun load(modelFile: File)

    /** Streams the assistant reply as text deltas. */
    fun generate(messages: List<ChatMessage>): Flow<String>

    /** Releases native resources. */
    fun close()
}
