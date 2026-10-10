package com.wendev.kolas.data.llm

import android.content.Context
import android.util.Log
import com.wendev.kolas.R
import com.wendev.kolas.data.preferences.LlmPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.IOException
import java.net.ConnectException
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import java.net.UnknownHostException
import java.util.concurrent.ConcurrentHashMap
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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Download status of a single chat model. */
sealed interface LlmModelStatus {

    /** No file on disk yet. */
    data object NotDownloaded : LlmModelStatus

    /** Download in flight; [progress] is 0f..1f, or null when the size is unknown. */
    data class Downloading(val progress: Float?) : LlmModelStatus

    /** The model file is present and usable. */
    data object Downloaded : LlmModelStatus

    /** The last download attempt failed. */
    data class Failed(val message: String) : LlmModelStatus
}

/** A catalog entry plus its live download status and selection flag. */
data class ChatModelState(
    val spec: LlmModelSpec,
    val status: LlmModelStatus,
    val isSelected: Boolean
)

/**
 * Owns every downloadable GGUF chat model: presence check, app-scoped download
 * with progress, cancel, and the persisted selection.
 */
@Singleton
class LlmModelManager @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val llmPreferences: LlmPreferences
) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val jobs = ConcurrentHashMap<String, Job>()

    private val statuses = MutableStateFlow(
        ChatModels.ALL.associate { spec ->
            spec.id to if (fileFor(spec).exists()) {
                LlmModelStatus.Downloaded
            } else {
                LlmModelStatus.NotDownloaded
            }
        }
    )
    private val selectedId = MutableStateFlow(ChatModels.DEFAULT.id)

    val models: StateFlow<List<ChatModelState>> =
        combine(statuses, selectedId) { byId, selected ->
            ChatModels.ALL.map { spec ->
                ChatModelState(
                    spec = spec,
                    status = byId[spec.id] ?: LlmModelStatus.NotDownloaded,
                    isSelected = spec.id == selected
                )
            }
        }.stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = ChatModels.ALL.map { spec ->
                ChatModelState(
                    spec = spec,
                    status = statuses.value[spec.id] ?: LlmModelStatus.NotDownloaded,
                    isSelected = spec.id == selectedId.value
                )
            }
        )

    init {
        scope.launch {
            llmPreferences.selectedModelId.collect { stored ->
                selectedId.value = ChatModels.byId(stored)?.id ?: ChatModels.DEFAULT.id
            }
        }
    }

    /** Absolute path of [spec]'s model file (valid once [LlmModelStatus.Downloaded]). */
    fun fileFor(spec: LlmModelSpec): File = File(File(context.filesDir, MODELS_DIR), spec.fileName)

    /** The currently selected model (falls back to the default). */
    fun selectedModel(): LlmModelSpec = ChatModels.byId(selectedId.value) ?: ChatModels.DEFAULT

    /** Starts the download for [modelId] when it is not already present. */
    fun download(modelId: String) {
        val spec = ChatModels.byId(modelId) ?: return
        if (fileFor(spec).exists()) {
            setStatus(spec.id, LlmModelStatus.Downloaded)
            return
        }
        if (jobs[modelId]?.isActive == true) return
        jobs[modelId] = scope.launch { runDownload(spec) }
    }

    /** Cancels an in-flight download and removes the partial file. */
    fun cancel(modelId: String) {
        jobs[modelId]?.cancel()
    }

    /** Persists [modelId] as the selected model. */
    fun select(modelId: String) {
        if (ChatModels.byId(modelId) == null) return
        scope.launch { llmPreferences.setSelectedModelId(modelId) }
    }

    private fun setStatus(id: String, status: LlmModelStatus) {
        statuses.update { current -> current + (id to status) }
    }

    private suspend fun runDownload(spec: LlmModelSpec) {
        val target = fileFor(spec)
        target.parentFile?.mkdirs()
        val temp = File(target.parentFile, "${target.name}.part")

        setStatus(spec.id, LlmModelStatus.Downloading(null))
        try {
            val connection = (URL(spec.url).openConnection() as HttpURLConnection).apply {
                instanceFollowRedirects = true
                connectTimeout = CONNECT_TIMEOUT_MS
                readTimeout = READ_TIMEOUT_MS
                setRequestProperty("User-Agent", USER_AGENT)
            }
            try {
                if (connection.responseCode !in 200..299) {
                    throw HttpStatusException(connection.responseCode)
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
                                setStatus(
                                    spec.id,
                                    LlmModelStatus.Downloading(
                                        (read.toFloat() / total).coerceIn(0f, 1f)
                                    )
                                )
                                lastNotified = read
                            }
                        }
                        output.flush()
                    }
                }
                if (total != null && temp.length() != total) {
                    throw IOException("Incomplete download")
                }
            } finally {
                connection.disconnect()
            }

            if (target.exists()) target.delete()
            if (!temp.renameTo(target)) error("Could not finalize the model file")
            setStatus(spec.id, LlmModelStatus.Downloaded)
        } catch (cancellation: CancellationException) {
            temp.delete()
            setStatus(spec.id, LlmModelStatus.NotDownloaded)
            throw cancellation
        } catch (throwable: Throwable) {
            temp.delete()
            Log.w(TAG, "Model download failed", throwable)
            setStatus(spec.id, LlmModelStatus.Failed(failureMessage(throwable)))
        } finally {
            jobs.remove(spec.id)
        }
    }

    /** Maps a download failure to a user-facing message. */
    private fun failureMessage(throwable: Throwable): String = when (throwable) {
        is HttpStatusException -> context.getString(R.string.chat_download_error_http, throwable.code)
        is UnknownHostException, is ConnectException, is SocketTimeoutException ->
            context.getString(R.string.chat_download_error_network)
        is IOException -> context.getString(R.string.chat_download_error_interrupted)
        else -> context.getString(R.string.chat_download_failed_generic)
    }

    /** Raised when the server answers with a non-2xx status. */
    private class HttpStatusException(val code: Int) : Exception("HTTP $code")

    private companion object {
        const val TAG = "LlmModelManager"
        const val MODELS_DIR = "models"
        const val CONNECT_TIMEOUT_MS = 15_000
        const val READ_TIMEOUT_MS = 30_000
        const val BUFFER_SIZE = 64 * 1024
        const val PROGRESS_STEP_BYTES = 1_000_000L
        const val USER_AGENT = "Kolas-Android"
    }
}
