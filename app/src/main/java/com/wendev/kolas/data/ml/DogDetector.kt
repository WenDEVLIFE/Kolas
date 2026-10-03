package com.wendev.kolas.data.ml

import android.graphics.Bitmap
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeler
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext

/** Outcome of a single dog-presence check. */
data class DogDetection(
    val isDog: Boolean,
    val label: String?,
    val confidence: Float
)

/**
 * Answers "is the captured frame a dog?" with ML Kit's on-device base image
 * labeler. Runs off the main thread; the labeler is created lazily and reused.
 */
@Singleton
class DogDetector @Inject constructor() {

    private val labeler: ImageLabeler by lazy {
        ImageLabeling.getClient(
            ImageLabelerOptions.Builder()
                .setConfidenceThreshold(DOG_CONFIDENCE_THRESHOLD)
                .build()
        )
    }

    /** Returns the first dog label at/above threshold, or a negative result. */
    suspend fun detect(bitmap: Bitmap): DogDetection = withContext(Dispatchers.Default) {
        val labels = labeler.process(InputImage.fromBitmap(bitmap, 0)).await()
        val dogLabel = labels.firstOrNull { DogLabels.isDog(it.text) }
        DogDetection(
            isDog = dogLabel != null,
            label = dogLabel?.text,
            confidence = dogLabel?.confidence ?: 0f
        )
    }

    private suspend fun <T> Task<T>.await(): T = suspendCancellableCoroutine { continuation ->
        addOnSuccessListener { result -> continuation.resume(result) }
        addOnFailureListener { error -> continuation.resumeWithException(error) }
        addOnCanceledListener { continuation.cancel() }
    }

    private companion object {
        const val DOG_CONFIDENCE_THRESHOLD = 0.5f
    }
}
