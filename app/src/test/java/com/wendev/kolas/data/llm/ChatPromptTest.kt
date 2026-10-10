package com.wendev.kolas.data.llm

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ChatPromptTest {

    @Test
    fun `system message leads the prompt`() {
        val messages = ChatPrompt.buildMessages(
            emotion = "happy",
            confidence = 0.9f,
            history = listOf(ChatMessage(ChatAuthor.USER, "hi"))
        )
        assertEquals(ChatAuthor.SYSTEM, messages.first().author)
    }

    @Test
    fun `system message carries guardrails and the reading`() {
        val system = ChatPrompt.buildMessages("angry", 0.8f, emptyList()).first().text
        assertTrue(system.contains("veterinary"))
        assertTrue(system.contains("ANGRY"))
        assertTrue(system.contains("80%"))
    }

    @Test
    fun `history is preserved after the system message`() {
        val history = listOf(ChatMessage(ChatAuthor.USER, "why?"))
        val messages = ChatPrompt.buildMessages("relax", 0.5f, history)
        assertEquals(history, messages.drop(1))
    }
}
