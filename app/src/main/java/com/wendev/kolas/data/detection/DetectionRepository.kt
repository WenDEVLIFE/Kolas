package com.wendev.kolas.data.detection

import android.graphics.Bitmap
import com.wendev.kolas.data.ml.EmotionResult
import kotlinx.coroutines.flow.Flow

/**
 * Boundary between the UI and detection storage.
 *
 * Storage is Room (SQLite) backed so readings survive process death. This
 * interface keeps the UI independent of the storage engine, so neither Scan nor
 * Result has to change when the implementation is swapped.
 */
interface DetectionRepository {

    /**
     * Persists the captured [bitmap] as a JPEG in app-private storage together
     * with [result], and returns the stored [Detection].
     */
    suspend fun save(bitmap: Bitmap, result: EmotionResult): Detection

    /** Returns the detection with [id], or `null` when it is not stored. */
    suspend fun get(id: String): Detection?

    /**
     * Emits every stored [Detection] whenever the set changes, so dashboards can
     * stay in sync with new scans. The current value is emitted immediately.
     */
    fun observeDetections(): Flow<List<Detection>>
}
