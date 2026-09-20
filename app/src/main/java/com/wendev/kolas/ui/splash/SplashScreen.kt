package com.wendev.kolas.ui.splash

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wendev.kolas.R
import com.wendev.kolas.ui.theme.KolasTheme

@Composable
fun SplashScreen(
    state: SplashUiState,
    modifier: Modifier = Modifier,
    onRetry: () -> Unit = {}
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            SplashLogo()
            Spacer(modifier = Modifier.height(24.dp))
            SplashBranding()
            Spacer(modifier = Modifier.height(32.dp))
            SplashStatus(state = state, onRetry = onRetry)
        }
    }
}

@Composable
private fun SplashLogo(modifier: Modifier = Modifier) {
    var appeared by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { appeared = true }

    val scale by animateFloatAsState(
        targetValue = if (appeared) 1f else 0.85f,
        animationSpec = tween(durationMillis = 600),
        label = "splash-logo-scale"
    )
    val alpha by animateFloatAsState(
        targetValue = if (appeared) 1f else 0f,
        animationSpec = tween(durationMillis = 600),
        label = "splash-logo-alpha"
    )

    Image(
        painter = painterResource(R.drawable.koalas_logo),
        contentDescription = stringResource(R.string.splash_logo_content_description),
        modifier = modifier
            .size(220.dp)
            .scale(scale)
            .alpha(alpha)
    )
}

@Composable
private fun SplashBranding(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // The logo lockup already carries the "KOLAS" wordmark, so the app name
        // is not repeated here — only the tagline is shown.
        Text(
            text = stringResource(R.string.splash_tagline),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SplashStatus(
    state: SplashUiState,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (state) {
        is SplashUiState.Loading -> SplashLoading(
            progress = state.progress,
            message = state.message,
            modifier = modifier
        )

        is SplashUiState.Error -> SplashError(
            message = state.message,
            onRetry = onRetry,
            modifier = modifier
        )

        SplashUiState.Ready -> Unit
    }
}

@Composable
private fun SplashLoading(
    progress: Float?,
    message: String?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val barModifier = Modifier
            .widthIn(max = 240.dp)
            .fillMaxWidth()

        if (progress == null) {
            LinearProgressIndicator(
                modifier = barModifier,
                color = MaterialTheme.colorScheme.primary
            )
        } else {
            LinearProgressIndicator(
                progress = { progress },
                modifier = barModifier,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = message ?: stringResource(R.string.splash_loading),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SplashError(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.splash_error_title),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onRetry,
            modifier = Modifier.heightIn(min = 48.dp)
        ) {
            Text(text = stringResource(R.string.splash_retry))
        }
    }
}

@Preview(name = "Splash - Loading", showBackground = true)
@Composable
private fun SplashLoadingPreview() {
    KolasTheme {
        SplashScreen(state = SplashUiState.Loading(progress = null, message = null))
    }
}

@Preview(name = "Splash - Loading (determinate)", showBackground = true)
@Composable
private fun SplashLoadingProgressPreview() {
    KolasTheme {
        SplashScreen(state = SplashUiState.Loading(progress = 0.6f, message = null))
    }
}

@Preview(name = "Splash - Error", showBackground = true)
@Composable
private fun SplashErrorPreview() {
    KolasTheme {
        SplashScreen(state = SplashUiState.Error(message = stringResource(R.string.splash_error_generic)))
    }
}

@Preview(name = "Splash - Ready", showBackground = true)
@Composable
private fun SplashReadyPreview() {
    KolasTheme {
        SplashScreen(state = SplashUiState.Ready)
    }
}
