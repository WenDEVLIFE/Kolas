package com.wendev.kolas.data.llm

import android.content.Context
import com.wendev.kolas.R
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Placeholder engine so the chat flow works before the native llama.cpp engine
 * lands. Streams a canned reply word by word. Replace the Hilt binding with the
 * real implementation later.
 */
@Singleton
class StubLlamaInference @Inject constructor(
    @param:ApplicationContext private val context: Context
) : LlamaInference {

    override suspend fun load(modelFile: File) = Unit

    override fun generate(messages: List<ChatMessage>): Flow<String> = flow {
        val reply = context.getString(R.string.chat_stub_reply)
        reply.split(" ").forEach { word ->
            delay(TOKEN_DELAY_MS)
            emit("$word ")
        }
    }

    override fun close() = Unit

    private companion object {
        const val TOKEN_DELAY_MS = 60L
    }
}
