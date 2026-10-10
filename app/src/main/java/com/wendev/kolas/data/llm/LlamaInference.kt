package com.wendev.kolas.data.llm

import java.io.File
import kotlinx.coroutines.flow.Flow

/**
 * Boundary to the on-device LLM engine. Stage 1 ships a stub; the llama.cpp JNI
 * implementation lands behind this same interface without touching the UI.
 */
interface LlamaInference {

    /** Loads [modelFile]; safe to call repeatedly (no-op if already loaded). */
    suspend fun load(modelFile: File)

    /** Streams the assistant reply as text deltas. */
    fun generate(messages: List<ChatMessage>): Flow<String>

    /** Releases native resources. */
    fun close()
}
