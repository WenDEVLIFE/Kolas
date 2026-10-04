package com.wendev.kolas.data.detection

import android.content.Context
import android.graphics.Bitmap
import com.wendev.kolas.data.ml.EmotionResult
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

/**
 * Slice 2 store: JPEGs go to app-private storage, the [Detection] metadata lives
 * in a process-local map. Readings are intentionally lost on process death —
 * Slice 4 replaces this with Room without touching the UI.
 */
@Singleton
class InMemoryDetectionRepository @Inject constructor(
    @param:ApplicationContext private val context: Context
) : DetectionRepository {

    private val lock = Mutex()
    private val detections = LinkedHashMap<String, Detection>()
    private val _detections = MutableStateFlow<List<Detection>>(emptyList())

    override fun observeDetections(): Flow<List<Detection>> = _detections.asStateFlow()

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
            lock.withLock {
                detections[id] = detection
                _detections.value = detections.values.toList()
            }
            detection
        }

    override suspend fun get(id: String): Detection? = withContext(Dispatchers.IO) {
        lock.withLock { detections[id] }
    }

    private companion object {
        const val DETECTIONS_DIR = "detections"
        const val JPEG_QUALITY = 92
    }
}
