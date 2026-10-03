package com.wendev.kolas.data.ml

/**
 * Output of a single emotion-classification pass.
 *
 * [emotion] is one of [DogEmotionClassifier.EMOTIONS] (lowercase), [confidence]
 * is the softmax probability of that class, and [allScores] carries every class
 * probability keyed by label for the Result breakdown.
 */
data class EmotionResult(
    val emotion: String,
    val confidence: Float,
    val allScores: Map<String, Float>
)
