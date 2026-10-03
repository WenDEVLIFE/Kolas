package com.wendev.kolas.ui.scan

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ScanRoute(
    onCaptured: (String) -> Unit,
    viewModel: ScanViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        viewModel.onPermissionResult(
            granted = granted,
            permanentlyDenied = !granted && !context.hasCameraRationale()
        )
    }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                ScanEvent.RequestCameraPermission ->
                    permissionLauncher.launch(Manifest.permission.CAMERA)

                is ScanEvent.Detected -> onCaptured(event.detectionId)
            }
        }
    }

    ScanScreen(
        state = state,
        onRetryPermission = viewModel::requestPermission,
        onOpenAppSettings = { context.openAppSettings() },
        onShutterClick = viewModel::startCapture,
        onImageCaptured = viewModel::onImageCaptured,
        onCaptureFailed = viewModel::onCaptureFailed,
        onRetry = viewModel::retry
    )
}

/**
 * True while the OS would still show the permission dialog. Once the user denies
 * permanently the launcher becomes a no-op, so the UI must route to app settings
 * instead of asking again.
 */
private fun Context.hasCameraRationale(): Boolean {
    val activity = this as? Activity ?: return true
    return ActivityCompat.shouldShowRequestPermissionRationale(
        activity,
        Manifest.permission.CAMERA
    )
}

private fun Context.openAppSettings() {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(intent)
}
