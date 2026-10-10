package com.wendev.kolas.ui.settings

import android.content.Context
import android.text.format.Formatter
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wendev.kolas.R
import com.wendev.kolas.data.llm.ChatModelState
import com.wendev.kolas.data.llm.LlmModelManager
import com.wendev.kolas.data.llm.ModelTier
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/** Backs the Settings → Models picker. */
@HiltViewModel
class ModelsViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val llmModelManager: LlmModelManager
) : ViewModel() {

    val state: StateFlow<ModelsUiState> = llmModelManager.models
        .map { models -> ModelsUiState.Content(models.map { it.toRow() }) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
            initialValue = ModelsUiState.Loading
        )

    fun onSelect(modelId: String) = llmModelManager.select(modelId)

    fun onDownload(modelId: String) = llmModelManager.download(modelId)

    fun onCancel(modelId: String) = llmModelManager.cancel(modelId)

    private fun ChatModelState.toRow(): ChatModelRow = ChatModelRow(
        id = spec.id,
        name = spec.displayName,
        summary = context.getString(
            R.string.settings_chat_model_summary,
            context.getString(spec.tier.labelRes()),
            Formatter.formatShortFileSize(context, spec.approxSizeBytes),
            spec.minRamGb
        ),
        status = status,
        isSelected = isSelected
    )

    @StringRes
    private fun ModelTier.labelRes(): Int = when (this) {
        ModelTier.FAST -> R.string.settings_model_tier_fast
        ModelTier.BALANCED -> R.string.settings_model_tier_balanced
        ModelTier.QUALITY -> R.string.settings_model_tier_quality
    }

    private companion object {
        const val STOP_TIMEOUT_MS = 5_000L
    }
}
