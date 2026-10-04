package com.wendev.kolas.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wendev.kolas.R
import com.wendev.kolas.ui.components.BackScaffold
import com.wendev.kolas.ui.theme.KolasTheme

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onOpenTheme: () -> Unit,
    onOpenModels: () -> Unit,
    onOpenAbout: () -> Unit,
    onOpenTerms: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackScaffold(
        title = stringResource(R.string.settings_title),
        onBack = onBack,
        modifier = modifier
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            SettingsMenuItem(
                title = stringResource(R.string.settings_item_theme),
                onClick = onOpenTheme
            )
            HorizontalDivider()
            SettingsMenuItem(
                title = stringResource(R.string.settings_item_models),
                onClick = onOpenModels
            )
            HorizontalDivider()
            SettingsMenuItem(
                title = stringResource(R.string.settings_item_about),
                onClick = onOpenAbout
            )
            HorizontalDivider()
            SettingsMenuItem(
                title = stringResource(R.string.settings_item_terms),
                onClick = onOpenTerms
            )
        }
    }
}

@Composable
private fun SettingsMenuItem(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .heightIn(min = 56.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(name = "Settings menu", showBackground = true)
@Composable
private fun SettingsMenuPreview() {
    KolasTheme {
        SettingsScreen(
            onBack = {},
            onOpenTheme = {},
            onOpenModels = {},
            onOpenAbout = {},
            onOpenTerms = {}
        )
    }
}
