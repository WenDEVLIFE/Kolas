package com.wendev.kolas.ui.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wendev.kolas.R
import com.wendev.kolas.data.detection.DetectionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Home dashboard. Counts are derived from [DetectionRepository.observeDetections],
 * so the dashboard refreshes as soon as a new scan is stored.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val detectionRepository: DetectionRepository
) : ViewModel() {

    private val _state = MutableStateFlow<HomeUiState>(
        HomeUiState.Content(todayCount = 0, totalCount = 0)
    )
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                detectionRepository.observeDetections().collect { detections ->
                    val startOfToday = startOfTodayMillis()
                    _state.value = HomeUiState.Content(
                        todayCount = detections.count { it.createdAt >= startOfToday },
                        totalCount = detections.size
                    )
                }
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (throwable: Throwable) {
                _state.value = HomeUiState.Error(
                    message = throwable.message ?: context.getString(R.string.home_error_title)
                )
            }
        }
    }

    private fun startOfTodayMillis(): Long {
        val zone = ZoneId.systemDefault()
        return Instant.now().atZone(zone).toLocalDate().atStartOfDay(zone).toInstant().toEpochMilli()
    }
}
