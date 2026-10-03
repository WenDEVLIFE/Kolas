package com.wendev.kolas.ui.scan

import android.graphics.Bitmap
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.wendev.kolas.R
import com.wendev.kolas.ui.components.PlaceholderContent
import com.wendev.kolas.ui.theme.KolasTheme

@Composable
fun ScanScreen(
    state: ScanUiState,
    onRetryPermission: () -> Unit,
    onOpenAppSettings: () -> Unit,
    onShutterClick: () -> Unit,
    onImageCaptured: (Bitmap) -> Unit,
    onCaptureFailed: (String?) -> Unit,
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

        is ScanUiState.PermissionDenied -> ScanPermissionDenied(
            permanentlyDenied = state.permanentlyDenied,
            onRetryPermission = onRetryPermission,
            onOpenAppSettings = onOpenAppSettings,
            modifier = modifier
        )

        ScanUiState.CameraReady, ScanUiState.Capturing -> ScanCameraContent(
            // A single call site keeps the CameraX AndroidView (and therefore the
            // camera binding) alive across the Ready -> Capturing transition; two
            // separate branches would tear the preview down mid-capture.
            capturing = state is ScanUiState.Capturing,
            onShutterClick = onShutterClick,
            onImageCaptured = onImageCaptured,
            onCaptureFailed = onCaptureFailed,
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

@Composable
private fun ScanPermissionDenied(
    permanentlyDenied: Boolean,
    onRetryPermission: () -> Unit,
    onOpenAppSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    PlaceholderContent(
        title = stringResource(R.string.scan_permission_denied_title),
        body = stringResource(
            if (permanentlyDenied) {
                R.string.scan_permission_denied_permanent
            } else {
                R.string.scan_permission_denied
            }
        ),
        actionLabel = stringResource(
            if (permanentlyDenied) R.string.scan_open_settings else R.string.scan_grant_permission
        ),
        onAction = if (permanentlyDenied) onOpenAppSettings else onRetryPermission,
        modifier = modifier
    )
}

@Composable
private fun ScanCameraContent(
    capturing: Boolean,
    onShutterClick: () -> Unit,
    onImageCaptured: (Bitmap) -> Unit,
    onCaptureFailed: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        if (LocalInspectionMode.current) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
        } else {
            CameraPreview(
                onImageCaptured = onImageCaptured,
                onCaptureFailed = onCaptureFailed,
                captureRequested = capturing,
                modifier = Modifier.fillMaxSize()
            )
        }

        ShutterButton(
            enabled = !capturing,
            onClick = onShutterClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        )

        if (capturing) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.32f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
private fun CameraPreview(
    onImageCaptured: (Bitmap) -> Unit,
    onCaptureFailed: (String?) -> Unit,
    captureRequested: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentOnImageCaptured by rememberUpdatedState(onImageCaptured)
    val currentOnCaptureFailed by rememberUpdatedState(onCaptureFailed)

    val controller = remember(context, lifecycleOwner) {
        ScanCameraController(
            context = context,
            lifecycleOwner = lifecycleOwner,
            onImageCaptured = { bitmap -> currentOnImageCaptured(bitmap) },
            onError = { message -> currentOnCaptureFailed(message) }
        )
    }

    AndroidView(
        modifier = modifier,
        factory = { previewContext ->
            PreviewView(previewContext).also { previewView ->
                previewView.scaleType = PreviewView.ScaleType.FILL_CENTER
                controller.bind(previewView)
            }
        }
    )

    LaunchedEffect(captureRequested) {
        if (captureRequested) controller.capture()
    }
}

@Composable
private fun ShutterButton(
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val description = stringResource(R.string.scan_shutter_content_description)
    Box(
        modifier = modifier
            .size(72.dp)
            .semantics {
                contentDescription = description
                role = Role.Button
            }
            .clip(CircleShape)
            .background(
                if (enabled) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }
            )
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(
                    if (enabled) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.outline
                    }
                )
        )
    }
}

@Preview(name = "Scan - Requesting permission", showBackground = true)
@Composable
private fun ScanRequestingPermissionPreview() {
    KolasTheme {
        ScanScreen(
            state = ScanUiState.RequestingPermission,
            onRetryPermission = {},
            onOpenAppSettings = {},
            onShutterClick = {},
            onImageCaptured = {},
            onCaptureFailed = {},
            onRetry = {}
        )
    }
}

@Preview(name = "Scan - Permission denied", showBackground = true)
@Composable
private fun ScanPermissionDeniedPreview() {
    KolasTheme {
        ScanScreen(
            state = ScanUiState.PermissionDenied(permanentlyDenied = false),
            onRetryPermission = {},
            onOpenAppSettings = {},
            onShutterClick = {},
            onImageCaptured = {},
            onCaptureFailed = {},
            onRetry = {}
        )
    }
}

@Preview(name = "Scan - Permission denied permanently", showBackground = true)
@Composable
private fun ScanPermissionDeniedPermanentPreview() {
    KolasTheme {
        ScanScreen(
            state = ScanUiState.PermissionDenied(permanentlyDenied = true),
            onRetryPermission = {},
            onOpenAppSettings = {},
            onShutterClick = {},
            onImageCaptured = {},
            onCaptureFailed = {},
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
            onRetryPermission = {},
            onOpenAppSettings = {},
            onShutterClick = {},
            onImageCaptured = {},
            onCaptureFailed = {},
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
            onRetryPermission = {},
            onOpenAppSettings = {},
            onShutterClick = {},
            onImageCaptured = {},
            onCaptureFailed = {},
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
            onRetryPermission = {},
            onOpenAppSettings = {},
            onShutterClick = {},
            onImageCaptured = {},
            onCaptureFailed = {},
            onRetry = {}
        )
    }
}
