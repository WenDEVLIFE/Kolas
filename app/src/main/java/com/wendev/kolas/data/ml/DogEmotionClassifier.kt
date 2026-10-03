package com.wendev.kolas.data.ml

import android.content.Context
import android.graphics.Bitmap
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.Closeable
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.tensorflow.lite.Interpreter

/**
 * Runs `assets/model.tflite` (5-class dog emotion classifier) on the CPU.
 *
 * ## Input contract — read this before touching [toInputBuffer]
 *
 * The model expects `float32` RGB pixels in the **raw `[0, 255]`** range for a
 * `224 x 224 x 3` input. Do **NOT** divide by 255 and do **NOT** normalize to
 * `[0, 1]` or `[-1, 1]`.
 *
 * Rescaling and Normalization are **baked into the graph itself** — the first
 * operators are MUL -> SUB -> MUL. Adding any normalization on the Kotlin side
 * applies it twice and collapses every class to roughly the same probability.
 * This was measured against the real model: raw `[0, 255]` scored 5/5 on one
 * dataset image per class, while both `[0, 1]` and `[-1, 1]` scored 1/5.
 *
 * Class order (index -> label) is alphabetical and matches `configs/config.yaml`.
 */
@Singleton
class DogEmotionClassifier @Inject constructor(
    @param:ApplicationContext private val context: Context
) : Closeable {

    private val runLock = Mutex()

    @Volatile
    private var interpreter: Interpreter? = null

    /**
     * Forces the interpreter to be constructed now. Must be called off the main
     * thread; safe to call repeatedly (the instance is cached).
     */
    suspend fun warmUp() {
        withContext(Dispatchers.Default) { obtainInterpreter() }
    }

    /**
     * Classifies one frame. Safe to call from any coroutine: the single
     * [Interpreter] is serialized with [runLock] because `Interpreter.run` is not
     * thread-safe, and a fresh input buffer is allocated per call.
     */
    suspend fun classify(bitmap: Bitmap): EmotionResult = withContext(Dispatchers.Default) {
        val input = toInputBuffer(bitmap)
        val output = Array(1) { FloatArray(EMOTIONS.size) }

        runLock.withLock {
            obtainInterpreter().run(input, output)
        }

        val scores = output[0]
        var bestIndex = 0
        for (index in scores.indices) {
            if (scores[index] > scores[bestIndex]) bestIndex = index
        }

        EmotionResult(
            emotion = EMOTIONS[bestIndex],
            confidence = scores[bestIndex],
            allScores = EMOTIONS.mapIndexed { index, label -> label to scores[index] }.toMap()
        )
    }

    override fun close() {
        synchronized(this) {
            interpreter?.close()
            interpreter = null
        }
    }

    private fun obtainInterpreter(): Interpreter {
        interpreter?.let { return it }
        synchronized(this) {
            interpreter?.let { return it }
            val options = Interpreter.Options().apply { setNumThreads(NUM_THREADS) }
            val created = Interpreter(loadModel(), options)
            interpreter = created
            return created
        }
    }

    /**
     * Reads the asset into a direct [ByteBuffer]. We deliberately do not use
     * `assets.openFd` because `.tflite` is not in the default no-compress list
     * and a compressed asset cannot be memory-mapped.
     */
    private fun loadModel(): ByteBuffer {
        val bytes = context.assets.open(MODEL_ASSET).use { it.readBytes() }
        return ByteBuffer.allocateDirect(bytes.size).apply {
            order(ByteOrder.nativeOrder())
            put(bytes)
            rewind()
        }
    }

    /**
     * Bilinear-downscales to [INPUT_SIZE] and writes RAW `[0, 255]` floats in RGB
     * order. See the class KDoc for why no further normalization is allowed.
     */
    private fun toInputBuffer(bitmap: Bitmap): ByteBuffer {
        val scaled = if (bitmap.width == INPUT_SIZE && bitmap.height == INPUT_SIZE) {
            bitmap
        } else {
            Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, true)
        }

        val pixels = IntArray(INPUT_SIZE * INPUT_SIZE)
        scaled.getPixels(pixels, 0, INPUT_SIZE, 0, 0, INPUT_SIZE, INPUT_SIZE)
        if (scaled !== bitmap) scaled.recycle()

        val buffer = ByteBuffer.allocateDirect(
            INPUT_SIZE * INPUT_SIZE * CHANNELS * Float.SIZE_BYTES
        )
        buffer.order(ByteOrder.nativeOrder())
        for (pixel in pixels) {
            buffer.putFloat(((pixel shr 16) and 0xFF).toFloat())
            buffer.putFloat(((pixel shr 8) and 0xFF).toFloat())
            buffer.putFloat((pixel and 0xFF).toFloat())
        }
        buffer.rewind()
        return buffer
    }

    companion object {
        /** Index order matches the model output; alphabetical as trained. */
        val EMOTIONS: List<String> = listOf("alert", "angry", "frown", "happy", "relax")

        private const val MODEL_ASSET = "model.tflite"
        private const val INPUT_SIZE = 224
        private const val CHANNELS = 3
        private const val NUM_THREADS = 4
    }
}
