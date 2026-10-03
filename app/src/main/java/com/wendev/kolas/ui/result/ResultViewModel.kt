package com.wendev.kolas.ui.result

import android.content.Context
import android.graphics.BitmapFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.wendev.kolas.R
import com.wendev.kolas.data.detection.DetectionRepository
import com.wendev.kolas.ui.navigation.ResultDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Loads a stored [com.wendev.kolas.data.detection.Detection] by the id carried in
 * the navigation route, together with its captured photo. The LLM explanation
 * flow arrives in Slice 3.
 */
@HiltViewModel
class ResultViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    @param:ApplicationContext private val context: Context,
    private val detectionRepository: DetectionRepository
) : ViewModel() {

    private val detectionId: String = checkNotNull(
        savedStateHandle.toRoute<ResultDestination>().detectionId
    )

    private val _state = MutableStateFlow<ResultUiState>(ResultUiState.Loading)
    val state: StateFlow<ResultUiState> = _state.asStateFlow()

    init {
        load()
    }

    fun retry() = load()

    private fun load() {
        _state.value = ResultUiState.Loading
        viewModelScope.launch {
            val detection = detectionRepository.get(detectionId)
            if (detection == null) {
                _state.value = ResultUiState.Error(context.getString(R.string.result_not_found))
                return@launch
            }

            val bitmap = withContext(Dispatchers.IO) {
                BitmapFactory.decodeFile(File(detection.imagePath).absolutePath)
            }
            if (bitmap == null) {
                _state.value = ResultUiState.Error(context.getString(R.string.result_image_missing))
                return@launch
            }

            _state.value = ResultUiState.Ready(detection = detection, bitmap = bitmap)
        }
    }
}
