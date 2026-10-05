package com.wendev.kolas.data.detection.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/** SQLite row for one stored emotion reading. */
@Entity(tableName = "detections")
data class DetectionEntity(
    @PrimaryKey val id: String,
    val imagePath: String,
    val emotion: String,
    val confidence: Float,
    val allScores: Map<String, Float>,
    val createdAt: Long
)
