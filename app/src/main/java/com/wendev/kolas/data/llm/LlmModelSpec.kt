package com.wendev.kolas.data.llm

/** Performance tier of a downloadable chat model, used for the picker copy. */
enum class ModelTier { FAST, BALANCED, QUALITY }

/** Describes a downloadable GGUF chat model. */
data class LlmModelSpec(
    val id: String,
    val displayName: String,
    val tier: ModelTier,
    val url: String,
    val fileName: String,
    val approxSizeBytes: Long,
    val minRamGb: Int
)

/** The chat models Kolas can download. */
object ChatModels {

    val FAST = LlmModelSpec(
        id = "gemma-3-1b",
        displayName = "Gemma 3 1B",
        tier = ModelTier.FAST,
        url = "https://huggingface.co/unsloth/gemma-3-1b-it-GGUF/resolve/main/gemma-3-1b-it-Q4_K_M.gguf",
        fileName = "gemma-3-1b-it-Q4_K_M.gguf",
        approxSizeBytes = 806_058_272L,
        minRamGb = 4
    )

    val BALANCED = LlmModelSpec(
        id = "qwen3-1.7b",
        displayName = "Qwen3 1.7B",
        tier = ModelTier.BALANCED,
        url = "https://huggingface.co/unsloth/Qwen3-1.7B-GGUF/resolve/main/Qwen3-1.7B-Q4_K_M.gguf",
        fileName = "Qwen3-1.7B-Q4_K_M.gguf",
        approxSizeBytes = 1_107_409_472L,
        minRamGb = 6
    )

    val QUALITY = LlmModelSpec(
        id = "qwen3-4b",
        displayName = "Qwen3 4B",
        tier = ModelTier.QUALITY,
        url = "https://huggingface.co/unsloth/Qwen3-4B-Instruct-2507-GGUF/resolve/main/Qwen3-4B-Instruct-2507-Q4_K_M.gguf",
        fileName = "Qwen3-4B-Instruct-2507-Q4_K_M.gguf",
        approxSizeBytes = 2_497_281_120L,
        minRamGb = 8
    )

    val ALL: List<LlmModelSpec> = listOf(FAST, BALANCED, QUALITY)

    val DEFAULT: LlmModelSpec = BALANCED

    fun byId(id: String?): LlmModelSpec? = ALL.firstOrNull { it.id == id }
}
