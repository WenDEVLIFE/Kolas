package com.wendev.kolas.data.llm

import android.content.Context
import android.util.Log
import com.wendev.kolas.R
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Status of the downloadable on-device chat model. */
sealed interface LlmModelStatus {

    /** No model file on disk yet. */
    data object Missing : LlmModelStatus

    /** Download in flight; [progress] is 0f..1f, or null when the size is unknown. */
    data class Downloading(val progress: Float?) : LlmModelStatus

    /** The model file is present and usable. */
    data object Ready : LlmModelStatus

    /** The last download attempt failed. */
    data class Failed(val message: String) : LlmModelStatus
}

/**
 * Owns the downloadable GGUF chat model on disk: checks presence, downloads it
 * from Hugging Face with progress, and cancels in-flight downloads.
 *
 * The download runs in an application-scoped coroutine so it survives leaving
 * the Chat screen. Bytes stream into a `*.part` file that is renamed on success.
 */
@Singleton
class LlmModelManager @Inject constructor(
    @param:ApplicationContext private val context: Context
) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _status = MutableStateFlow<LlmModelStatus>(
        if (modelFile().exists()) LlmModelStatus.Ready else LlmModelStatus.Missing
    )
    val status: StateFlow<LlmModelStatus> = _status.asStateFlow()

    private var downloadJob: Job? = null

    /** Absolute path of the model file (valid once [LlmModelStatus.Ready]). */
    fun modelFile(): File = File(File(context.filesDir, MODELS_DIR), spec.fileName)

    /** Starts the download when the model is not already present. */
    fun download() {
        if (modelFile().exists()) {
            _status.value = LlmModelStatus.Ready
            return
        }
        if (downloadJob?.isActive == true) return
        downloadJob = scope.launch { runDownload() }
    }

    /** Cancels an in-flight download and removes the partial file. */
    fun cancel() {
        downloadJob?.cancel()
    }

    private suspend fun runDownload() {
        val target = modelFile()
        target.parentFile?.mkdirs()
        val temp = File(target.parentFile, "${target.name}.part")

        _status.value = LlmModelStatus.Downloading(null)
        try {
            val connection = (URL(spec.url).openConnection() as HttpURLConnection).apply {
                instanceFollowRedirects = true
                connectTimeout = CONNECT_TIMEOUT_MS
                readTimeout = READ_TIMEOUT_MS
                setRequestProperty("User-Agent", USER_AGENT)
            }
            try {
                if (connection.responseCode !in 200..299) {
                    error("HTTP ${connection.responseCode}")
                }
                val total = connection.contentLengthLong.takeIf { it > 0L }
                connection.inputStream.use { input ->
                    temp.outputStream().use { output ->
                        val buffer = ByteArray(BUFFER_SIZE)
                        var read = 0L
                        var lastNotified = 0L
                        while (true) {
                            currentCoroutineContext().ensureActive()
                            val count = input.read(buffer)
                            if (count < 0) break
                            output.write(buffer, 0, count)
                            read += count
                            if (total != null && read - lastNotified >= PROGRESS_STEP_BYTES) {
                                _status.value = LlmModelStatus.Downloading(
                                    (read.toFloat() / total).coerceIn(0f, 1f)
                                )
                                lastNotified = read
                            }
                        }
                        output.flush()
                    }
                }
                if (total != null && temp.length() != total) {
                    error("Incomplete download")
                }
            } finally {
                connection.disconnect()
            }

            if (target.exists()) target.delete()
            if (!temp.renameTo(target)) error("Could not finalize the model file")
            _status.value = LlmModelStatus.Ready
        } catch (cancellation: CancellationException) {
            temp.delete()
            _status.value = LlmModelStatus.Missing
            throw cancellation
        } catch (throwable: Throwable) {
            temp.delete()
            Log.w(TAG, "Model download failed", throwable)
            _status.value = LlmModelStatus.Failed(
                context.getString(R.string.chat_download_failed_generic)
            )
        }
    }

    private companion object {
        const val TAG = "LlmModelManager"
        const val MODELS_DIR = "models"
        const val CONNECT_TIMEOUT_MS = 15_000
        const val READ_TIMEOUT_MS = 30_000
        const val BUFFER_SIZE = 64 * 1024
        const val PROGRESS_STEP_BYTES = 1_000_000L
        const val USER_AGENT = "Kolas-Android"
        val spec = ChatModels.DEFAULT
    }
}
