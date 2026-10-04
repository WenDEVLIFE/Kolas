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
fun ThemeScreen(
    state: ThemeUiState,
    onBack: () -> Unit,
    onThemeModeSelected: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier
) {
    BackScaffold(
        title = stringResource(R.string.settings_item_theme),
        onBack = onBack,
        modifier = modifier
    ) {
        when (state) {
            ThemeUiState.Loading -> PlaceholderContent(
                title = stringResource(R.string.settings_item_theme),
                body = stringResource(R.string.settings_loading),
                showProgress = true
            )

            is ThemeUiState.Content -> ThemePicker(
                selected = state.themeMode,
                onThemeModeSelected = onThemeModeSelected,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThemePicker(
    selected: ThemeMode,
    onThemeModeSelected: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
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

@StringRes
private fun ThemeMode.labelRes(): Int = when (this) {
    ThemeMode.SYSTEM -> R.string.settings_theme_system
    ThemeMode.LIGHT -> R.string.settings_theme_light
    ThemeMode.DARK -> R.string.settings_theme_dark
}

@Preview(name = "Theme - Loading", showBackground = true)
@Composable
private fun ThemeLoadingPreview() {
    KolasTheme {
        ThemeScreen(
            state = ThemeUiState.Loading,
            onBack = {},
            onThemeModeSelected = {}
        )
    }
}

@Preview(name = "Theme - System", showBackground = true)
@Composable
private fun ThemeSystemPreview() {
    KolasTheme {
        ThemeScreen(
            state = ThemeUiState.Content(themeMode = ThemeMode.SYSTEM),
            onBack = {},
            onThemeModeSelected = {}
        )
    }
}

@Preview(name = "Theme - Dark", showBackground = true)
@Composable
private fun ThemeDarkPreview() {
    KolasTheme(themeMode = ThemeMode.DARK) {
        ThemeScreen(
            state = ThemeUiState.Content(themeMode = ThemeMode.DARK),
            onBack = {},
            onThemeModeSelected = {}
        )
    }
}
