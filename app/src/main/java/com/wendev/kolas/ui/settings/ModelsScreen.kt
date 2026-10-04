package com.wendev.kolas.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wendev.kolas.R
import com.wendev.kolas.ui.components.BackScaffold
import com.wendev.kolas.ui.theme.KolasTheme

@Composable
fun ModelsScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    BackScaffold(
        title = stringResource(R.string.settings_item_models),
        onBack = onBack,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ModelRow(
                name = stringResource(R.string.settings_model_emotion_name),
                description = stringResource(R.string.settings_model_emotion_description)
            )
            ModelRow(
                name = stringResource(R.string.settings_model_detector_name),
                description = stringResource(R.string.settings_model_detector_description)
            )
        }
    }
}

@Composable
private fun ModelRow(
    name: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = stringResource(R.string.settings_model_status_ready),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Preview(name = "Models", showBackground = true)
@Composable
private fun ModelsPreview() {
    KolasTheme {
        ModelsScreen(onBack = {})
    }
}
