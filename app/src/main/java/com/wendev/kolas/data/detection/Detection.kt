package com.wendev.kolas.data.detection

/**
 * A single stored emotion reading.
 *
 * [imagePath] points at the captured JPEG inside app-private storage. The
 * per-class [allScores] map is kept so the Result screen can render a breakdown
 * without re-running inference.
 */
data class Detection(
    val id: String,
    val imagePath: String,
    val emotion: String,
    val confidence: Float,
    val allScores: Map<String, Float>,
    val createdAt: Long
)
