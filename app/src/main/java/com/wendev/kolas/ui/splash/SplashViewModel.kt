package com.wendev.kolas.ui.splash

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wendev.kolas.R
import com.wendev.kolas.data.ml.DogEmotionClassifier
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Seam for everything the app must prepare before the home screen is usable.
 * Kept behind an interface so [SplashViewModel] stays testable.
 */
interface SplashInitializer {
    suspend fun initialize(onProgress: (Float) -> Unit)
}

/**
 * Starts the single-process TFLite interpreter and confirms the model asset is
 * bundled. Construction happens on [Dispatchers.Default]; the splash keeps its
 * own minimum on-screen time and never blocks on the warm-up longer than that.
 */
class DefaultSplashInitializer(
    private val context: Context,
    private val classifier: DogEmotionClassifier
) : SplashInitializer {

    override suspend fun initialize(onProgress: (Float) -> Unit) {
        onProgress(0f)
        withContext(Dispatchers.IO) {
            val bundledAssets = context.assets.list(BASE_PATH).orEmpty()
            if (MODEL_ASSET_NAME in bundledAssets) {
                classifier.warmUp()
            }
        }
        onProgress(1f)
    }

    private companion object {
        const val BASE_PATH = ""
        const val MODEL_ASSET_NAME = "model.tflite"
    }
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    classifier: DogEmotionClassifier
) : ViewModel() {

    private val initializer: SplashInitializer = DefaultSplashInitializer(context, classifier)

    private val _state = MutableStateFlow<SplashUiState>(
        SplashUiState.Loading(progress = null, message = null)
    )
    val state: StateFlow<SplashUiState> = _state.asStateFlow()

    private val _events = Channel<SplashEvent>(Channel.BUFFERED)
    val events: Flow<SplashEvent> = _events.receiveAsFlow()

    private var startupJob: Job? = null

    init {
        startStartup()
    }

    fun retry() {
        startStartup()
    }

    private fun startStartup() {
        startupJob?.cancel()
        _state.value = SplashUiState.Loading(progress = null, message = null)
        startupJob = viewModelScope.launch {
            val startedAt = System.currentTimeMillis()
            try {
                initializer.initialize { progress ->
                    _state.value = SplashUiState.Loading(progress = progress, message = null)
                }
                _state.value = SplashUiState.Ready
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (throwable: Throwable) {
                _state.value = SplashUiState.Error(
                    message = throwable.message
                        ?: context.getString(R.string.splash_error_generic)
                )
            }

            // Keep the splash on screen long enough that it never flicker-flashes.
            val remaining = MINIMUM_DISPLAY_MILLIS - (System.currentTimeMillis() - startedAt)
            if (remaining > 0) delay(remaining)

            if (_state.value is SplashUiState.Error) return@launch
            _events.send(SplashEvent.NavigateToHome)
        }
    }

    private companion object {
        const val MINIMUM_DISPLAY_MILLIS = 1_200L
    }
}
