package com.wendev.kolas.ui.scan

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.wendev.kolas.R
import com.wendev.kolas.ui.components.PlaceholderContent
import com.wendev.kolas.ui.theme.KolasTheme

@Composable
fun ScanScreen(
    state: ScanUiState,
    onGrantPermission: () -> Unit,
    onStartCapture: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (state) {
        ScanUiState.RequestingPermission -> PlaceholderContent(
            title = stringResource(R.string.scan_requesting_permission_title),
            body = stringResource(R.string.scan_requesting_permission),
            showProgress = true,
            modifier = modifier
        )

        ScanUiState.PermissionDenied -> PlaceholderContent(
            title = stringResource(R.string.scan_permission_denied_title),
            body = stringResource(R.string.scan_permission_denied),
            actionLabel = stringResource(R.string.scan_grant_permission),
            onAction = onGrantPermission,
            modifier = modifier
        )

        ScanUiState.CameraReady -> PlaceholderContent(
            title = stringResource(R.string.scan_title),
            body = stringResource(R.string.scan_camera_ready),
            actionLabel = stringResource(R.string.scan_start),
            onAction = onStartCapture,
            modifier = modifier
        )

        ScanUiState.Capturing -> PlaceholderContent(
            title = stringResource(R.string.scan_title),
            body = stringResource(R.string.scan_capturing),
            showProgress = true,
            modifier = modifier
        )

        is ScanUiState.Error -> PlaceholderContent(
            title = stringResource(R.string.scan_error_title),
            body = state.message,
            actionLabel = stringResource(R.string.scan_retry),
            onAction = onRetry,
            modifier = modifier
        )
    }
}

@Preview(name = "Scan - Requesting permission", showBackground = true)
@Composable
private fun ScanRequestingPermissionPreview() {
    KolasTheme {
        ScanScreen(
            state = ScanUiState.RequestingPermission,
            onGrantPermission = {},
            onStartCapture = {},
            onRetry = {}
        )
    }
}

@Preview(name = "Scan - Permission denied", showBackground = true)
@Composable
private fun ScanPermissionDeniedPreview() {
    KolasTheme {
        ScanScreen(
            state = ScanUiState.PermissionDenied,
            onGrantPermission = {},
            onStartCapture = {},
            onRetry = {}
        )
    }
}

@Preview(name = "Scan - Camera ready", showBackground = true)
@Composable
private fun ScanCameraReadyPreview() {
    KolasTheme {
        ScanScreen(
            state = ScanUiState.CameraReady,
            onGrantPermission = {},
            onStartCapture = {},
            onRetry = {}
        )
    }
}

@Preview(name = "Scan - Capturing", showBackground = true)
@Composable
private fun ScanCapturingPreview() {
    KolasTheme {
        ScanScreen(
            state = ScanUiState.Capturing,
            onGrantPermission = {},
            onStartCapture = {},
            onRetry = {}
        )
    }
}

@Preview(name = "Scan - Error", showBackground = true)
@Composable
private fun ScanErrorPreview() {
    KolasTheme {
        ScanScreen(
            state = ScanUiState.Error(message = stringResource(R.string.scan_error_generic)),
            onGrantPermission = {},
            onStartCapture = {},
            onRetry = {}
        )
    }
}
