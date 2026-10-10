package com.wendev.kolas.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ModelsRoute(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ModelsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ModelsScreen(
        state = state,
        onBack = onBack,
        onSelect = viewModel::onSelect,
        onDownload = viewModel::onDownload,
        onCancel = viewModel::onCancel,
        modifier = modifier
    )
}
