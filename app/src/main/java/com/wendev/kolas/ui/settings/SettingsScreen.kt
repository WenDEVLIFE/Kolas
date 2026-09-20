package com.wendev.kolas.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.wendev.kolas.R
import com.wendev.kolas.ui.components.BackScaffold
import com.wendev.kolas.ui.components.PlaceholderContent
import com.wendev.kolas.ui.theme.KolasTheme

@Composable
fun SettingsScreen(
    state: SettingsUiState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackScaffold(
        title = stringResource(R.string.settings_title),
        onBack = onBack,
        modifier = modifier
    ) {
        when (state) {
            SettingsUiState.Loading -> PlaceholderContent(
                title = stringResource(R.string.settings_title),
                body = stringResource(R.string.settings_loading),
                showProgress = true
            )

            is SettingsUiState.Content -> PlaceholderContent(
                title = stringResource(R.string.settings_model_status),
                body = stringResource(
                    if (state.modelInstalled) R.string.settings_model_installed
                    else R.string.settings_model_missing
                )
            )

            is SettingsUiState.Downloading -> PlaceholderContent(
                title = stringResource(R.string.settings_title),
                body = stringResource(R.string.settings_downloading),
                showProgress = true
            )
        }
    }
}

@Preview(name = "Settings - Loading", showBackground = true)
@Composable
private fun SettingsLoadingPreview() {
    KolasTheme {
        SettingsScreen(state = SettingsUiState.Loading, onBack = {})
    }
}

@Preview(name = "Settings - Content", showBackground = true)
@Composable
private fun SettingsContentPreview() {
    KolasTheme {
        SettingsScreen(
            state = SettingsUiState.Content(modelInstalled = true, modelVersion = "1.0"),
            onBack = {}
        )
    }
}

@Preview(name = "Settings - Downloading", showBackground = true)
@Composable
private fun SettingsDownloadingPreview() {
    KolasTheme {
        SettingsScreen(state = SettingsUiState.Downloading(progress = 0.4f), onBack = {})
    }
}
