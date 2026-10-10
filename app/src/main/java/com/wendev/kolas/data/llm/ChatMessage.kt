package com.wendev.kolas.data.llm

/** Who produced a chat message. SYSTEM carries the guardrails, never shown in the UI. */
enum class ChatAuthor { SYSTEM, USER, KOLAS }

/** One message in the chat. */
data class ChatMessage(
    val author: ChatAuthor,
    val text: String
)
