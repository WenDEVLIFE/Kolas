package com.wendev.kolas.ui.scan

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wendev.kolas.R
import com.wendev.kolas.data.detection.DetectionRepository
import com.wendev.kolas.data.ml.DogDetector
import com.wendev.kolas.data.ml.DogEmotionClassifier
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * Owns the Scan state machine: permission -> live preview -> capture ->
 * classify -> persist -> emit [ScanEvent.Detected]. It never navigates; the
 * Route turns the event into a navigation call.
 */
@HiltViewModel
class ScanViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val classifier: DogEmotionClassifier,
    private val dogDetector: DogDetector,
    private val detectionRepository: DetectionRepository
) : ViewModel() {

    private val _state = MutableStateFlow<ScanUiState>(ScanUiState.RequestingPermission)
    val state: StateFlow<ScanUiState> = _state.asStateFlow()

    private val _events = Channel<ScanEvent>(Channel.BUFFERED)
    val events: Flow<ScanEvent> = _events.receiveAsFlow()

    init {
        if (hasCameraPermission()) {
            _state.value = ScanUiState.CameraReady
        } else {
            requestPermission()
        }
    }

    /** Shows the runtime permission dialog; also used by the "grant access" CTA. */
    fun requestPermission() {
        _state.value = ScanUiState.RequestingPermission
        viewModelScope.launch { _events.send(ScanEvent.RequestCameraPermission) }
    }

    fun onPermissionResult(granted: Boolean, permanentlyDenied: Boolean) {
        _state.value = if (granted) {
            ScanUiState.CameraReady
        } else {
            ScanUiState.PermissionDenied(permanentlyDenied = permanentlyDenied)
        }
    }

    /** Shutter tap. The screen observes [ScanUiState.Capturing] and fires the shot. */
    fun startCapture() {
        if (_state.value is ScanUiState.CameraReady) {
            _state.value = ScanUiState.Capturing
        }
    }

    /** Called by the camera before the frame is lost to process death. */
    fun onImageCaptured(bitmap: Bitmap) {
        if (_state.value !is ScanUiState.Capturing) return
        viewModelScope.launch {
            try {
                val dog = dogDetector.detect(bitmap)
                if (!dog.isDog) {
                    _state.value = ScanUiState.CameraReady
                    _events.send(ScanEvent.NotADog)
                    return@launch
                }
                val result = classifier.classify(bitmap)
                val detection = detectionRepository.save(bitmap, result)
                _state.value = ScanUiState.CameraReady
                _events.send(ScanEvent.Detected(detection.id))
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (throwable: Throwable) {
                _state.value = ScanUiState.Error(
                    message = throwable.message ?: context.getString(R.string.scan_error_generic)
                )
            }
        }
    }

    fun onCaptureFailed(message: String?) {
        _state.value = ScanUiState.Error(
            message = message ?: context.getString(R.string.scan_error_camera)
        )
    }

    fun retry() {
        if (hasCameraPermission()) {
            _state.value = ScanUiState.CameraReady
        } else {
            requestPermission()
        }
    }

    private fun hasCameraPermission(): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
            PackageManager.PERMISSION_GRANTED
}
