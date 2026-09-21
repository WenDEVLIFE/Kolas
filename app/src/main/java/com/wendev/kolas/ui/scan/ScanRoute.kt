package com.wendev.kolas.ui.scan

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ScanRoute(
    onCaptured: (String) -> Unit,
    viewModel: ScanViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is ScanEvent.Detected -> onCaptured(event.detectionId)
            }
        }
    }

    ScanScreen(
        state = state,
        onGrantPermission = { viewModel.onPermissionResult(granted = true) },
        onStartCapture = viewModel::startCapture,
        onRetry = viewModel::retry
    )
}
