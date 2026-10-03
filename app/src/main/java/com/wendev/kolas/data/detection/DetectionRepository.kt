package com.wendev.kolas.data.detection

import android.graphics.Bitmap
import com.wendev.kolas.data.ml.EmotionResult

/**
 * Boundary between the UI and detection storage.
 *
 * Slice 2 ships an in-memory implementation; Slice 4 swaps in a Room-backed one
 * behind this same interface so neither Scan nor Result has to change.
 */
interface DetectionRepository {

    /**
     * Persists the captured [bitmap] as a JPEG in app-private storage together
     * with [result], and returns the stored [Detection].
     */
    suspend fun save(bitmap: Bitmap, result: EmotionResult): Detection

    /** Returns the detection with [id], or `null` when it is not stored. */
    suspend fun get(id: String): Detection?
}
