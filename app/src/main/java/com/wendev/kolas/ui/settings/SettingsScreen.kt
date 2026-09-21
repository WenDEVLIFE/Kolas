package com.wendev.kolas.ui.settings

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wendev.kolas.R
import com.wendev.kolas.data.preferences.ThemeMode
import com.wendev.kolas.ui.components.BackScaffold
import com.wendev.kolas.ui.components.PlaceholderContent
import com.wendev.kolas.ui.theme.KolasTheme

@Composable
fun SettingsScreen(
    state: SettingsUiState,
    onBack: () -> Unit,
    onThemeModeSelected: (ThemeMode) -> Unit,
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

            is SettingsUiState.Content -> SettingsContent(
                state = state,
                onThemeModeSelected = onThemeModeSelected
            )

            is SettingsUiState.Downloading -> PlaceholderContent(
                title = stringResource(R.string.settings_title),
                body = stringResource(R.string.settings_downloading),
                showProgress = true
            )
        }
    }
}

@Composable
private fun SettingsContent(
    state: SettingsUiState.Content,
    onThemeModeSelected: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        ThemeSection(
            selected = state.themeMode,
            onThemeModeSelected = onThemeModeSelected
        )
        ModelSection(
            installed = state.modelInstalled,
            version = state.modelVersion
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThemeSection(
    selected: ThemeMode,
    onThemeModeSelected: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.settings_appearance_title),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = stringResource(R.string.settings_theme_label),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            ThemeMode.entries.forEachIndexed { index, mode ->
                SegmentedButton(
                    selected = mode == selected,
                    onClick = { onThemeModeSelected(mode) },
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = ThemeMode.entries.size
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Text(text = stringResource(mode.labelRes()))
                }
            }
        }
    }
}

@Composable
private fun ModelSection(
    installed: Boolean,
    version: String?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.settings_model_status),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = when {
                installed && version != null ->
                    stringResource(R.string.settings_model_installed_version, version)

                installed -> stringResource(R.string.settings_model_installed)
                else -> stringResource(R.string.settings_model_missing)
            },
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@StringRes
private fun ThemeMode.labelRes(): Int = when (this) {
    ThemeMode.SYSTEM -> R.string.settings_theme_system
    ThemeMode.LIGHT -> R.string.settings_theme_light
    ThemeMode.DARK -> R.string.settings_theme_dark
}

@Preview(name = "Settings - Loading", showBackground = true)
@Composable
private fun SettingsLoadingPreview() {
    KolasTheme {
        SettingsScreen(
            state = SettingsUiState.Loading,
            onBack = {},
            onThemeModeSelected = {}
        )
    }
}

@Preview(name = "Settings - Content (system, installed)", showBackground = true)
@Composable
private fun SettingsContentPreview() {
    KolasTheme {
        SettingsScreen(
            state = SettingsUiState.Content(
                themeMode = ThemeMode.SYSTEM,
                modelInstalled = true,
                modelVersion = "1.0"
            ),
            onBack = {},
            onThemeModeSelected = {}
        )
    }
}

@Preview(name = "Settings - Content (light selected)", showBackground = true)
@Composable
private fun SettingsContentLightPreview() {
    KolasTheme(themeMode = ThemeMode.LIGHT) {
        SettingsScreen(
            state = SettingsUiState.Content(
                themeMode = ThemeMode.LIGHT,
                modelInstalled = false,
                modelVersion = null
            ),
            onBack = {},
            onThemeModeSelected = {}
        )
    }
}

@Preview(name = "Settings - Content (dark selected)", showBackground = true)
@Composable
private fun SettingsContentDarkPreview() {
    KolasTheme(themeMode = ThemeMode.DARK) {
        SettingsScreen(
            state = SettingsUiState.Content(
                themeMode = ThemeMode.DARK,
                modelInstalled = true,
                modelVersion = null
            ),
            onBack = {},
            onThemeModeSelected = {}
        )
    }
}

@Preview(name = "Settings - Downloading", showBackground = true)
@Composable
private fun SettingsDownloadingPreview() {
    KolasTheme {
        SettingsScreen(
            state = SettingsUiState.Downloading(progress = 0.4f),
            onBack = {},
            onThemeModeSelected = {}
        )
    }
}
