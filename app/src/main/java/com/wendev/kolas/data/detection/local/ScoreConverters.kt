package com.wendev.kolas.data.detection.local

import androidx.room.TypeConverter
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

/** Stores the per-class score map as a JSON string column. */
class ScoreConverters {

    @TypeConverter
    fun fromScores(scores: Map<String, Float>): String =
        Json.encodeToString(SCORES_SERIALIZER, scores)

    @TypeConverter
    fun toScores(json: String): Map<String, Float> =
        runCatching { Json.decodeFromString(SCORES_SERIALIZER, json) }
            .getOrDefault(emptyMap())

    private companion object {
        val SCORES_SERIALIZER = MapSerializer(String.serializer(), Float.serializer())
    }
}
