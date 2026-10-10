package com.wendev.kolas.data.llm

/** Describes a downloadable GGUF chat model. */
data class LlmModelSpec(
    val displayName: String,
    val url: String,
    val fileName: String
)

/** The chat models Kolas can download. One default for now. */
object ChatModels {

    val DEFAULT = LlmModelSpec(
        displayName = "Qwen3 1.7B (Q4_K_M)",
        url = "https://huggingface.co/unsloth/Qwen3-1.7B-GGUF/resolve/main/Qwen3-1.7B-Q4_K_M.gguf?download=true",
        fileName = "Qwen3-1.7B-Q4_K_M.gguf"
    )
}
