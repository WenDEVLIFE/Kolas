package com.wendev.kolas.ui.history

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wendev.kolas.R
import com.wendev.kolas.data.detection.Detection
import com.wendev.kolas.data.detection.DetectionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Instant
import java.time.ZoneId
import java.util.Date
import javax.inject.Inject
import kotlin.math.roundToInt
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * History tab. Mirrors [DetectionRepository.observeDetections] into a
 * newest-first list, so every scan shows up here as soon as it is stored.
 */
@HiltViewModel
class HistoryViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val detectionRepository: DetectionRepository
) : ViewModel() {

    private val _state = MutableStateFlow<HistoryUiState>(HistoryUiState.Empty)
    val state: StateFlow<HistoryUiState> = _state.asStateFlow()

    private var observeJob: Job? = null

    init {
        observe()
    }

    /** Restarts the collection; used by the error state's retry action. */
    fun retry() {
        observe()
    }

    private fun observe() {
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            try {
                detectionRepository.observeDetections().collect { detections ->
                    _state.value = if (detections.isEmpty()) {
                        HistoryUiState.Empty
                    } else {
                        HistoryUiState.Content(
                            items = detections
                                .sortedByDescending { it.createdAt }
                                .map { it.toHistoryItem() }
                        )
                    }
                }
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (throwable: Throwable) {
                _state.value = HistoryUiState.Error(
                    message = throwable.message ?: context.getString(R.string.history_error_generic)
                )
            }
        }
    }

    private fun Detection.toHistoryItem(): HistoryItem = HistoryItem(
        id = id,
        emotion = emotion,
        summary = context.getString(
            R.string.history_item_meta,
            context.getString(R.string.result_confidence, (confidence * 100).roundToInt()),
            formatTimestamp(createdAt)
        )
    )

    /** "Today, 12:46 PM" for same-day readings, "Jul 4, 12:46 PM" otherwise. */
    private fun formatTimestamp(createdAt: Long): String {
        val timeFormat = android.text.format.DateFormat.getTimeFormat(context)
        val instant = Instant.ofEpochMilli(createdAt)
        val zone = ZoneId.systemDefault()
        val time = timeFormat.format(Date.from(instant))
        val isToday = instant.atZone(zone).toLocalDate() ==
            Instant.now().atZone(zone).toLocalDate()
        return if (isToday) {
            context.getString(R.string.history_time_today, time)
        } else {
            val dateFormat = android.text.format.DateFormat.getMediumDateFormat(context)
            context.getString(
                R.string.history_time_other,
                dateFormat.format(Date.from(instant)),
                time
            )
        }
    }
}
