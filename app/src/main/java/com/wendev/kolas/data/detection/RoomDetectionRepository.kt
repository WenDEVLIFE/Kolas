package com.wendev.kolas.data.detection

import android.content.Context
import android.graphics.Bitmap
import com.wendev.kolas.data.detection.local.DetectionDao
import com.wendev.kolas.data.detection.local.DetectionEntity
import com.wendev.kolas.data.ml.EmotionResult
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * Room-backed detection storage. JPEGs stay in app-private storage; the metadata
 * lives in SQLite and survives process death.
 */
@Singleton
class RoomDetectionRepository @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val detectionDao: DetectionDao
) : DetectionRepository {

    override suspend fun save(bitmap: Bitmap, result: EmotionResult): Detection =
        withContext(Dispatchers.IO) {
            val id = UUID.randomUUID().toString()
            val directory = File(context.filesDir, DETECTIONS_DIR).apply { mkdirs() }
            val file = File(directory, "$id.jpg")

            file.outputStream().use { output ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, output)
            }

            val detection = Detection(
                id = id,
                imagePath = file.absolutePath,
                emotion = result.emotion,
                confidence = result.confidence,
                allScores = result.allScores,
                createdAt = System.currentTimeMillis()
            )
            detectionDao.insert(detection.toEntity())
            detection
        }

    override suspend fun get(id: String): Detection? = withContext(Dispatchers.IO) {
        detectionDao.getById(id)?.toDetection()
    }

    override fun observeDetections(): Flow<List<Detection>> =
        detectionDao.observeAll().map { entities -> entities.map { it.toDetection() } }

    private fun Detection.toEntity(): DetectionEntity = DetectionEntity(
        id = id,
        imagePath = imagePath,
        emotion = emotion,
        confidence = confidence,
        allScores = allScores,
        createdAt = createdAt
    )

    private fun DetectionEntity.toDetection(): Detection = Detection(
        id = id,
        imagePath = imagePath,
        emotion = emotion,
        confidence = confidence,
        allScores = allScores,
        createdAt = createdAt
    )

    private companion object {
        const val DETECTIONS_DIR = "detections"
        const val JPEG_QUALITY = 92
    }
}
