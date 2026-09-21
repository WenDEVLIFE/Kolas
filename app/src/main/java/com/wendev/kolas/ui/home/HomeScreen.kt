package com.wendev.kolas.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wendev.kolas.R
import com.wendev.kolas.ui.components.PlaceholderContent
import com.wendev.kolas.ui.history.HistoryItem
import com.wendev.kolas.ui.theme.KolasTheme

@Composable
fun HomeScreen(
    state: HomeUiState,
    onScanClick: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (state) {
        HomeUiState.Empty -> HomeEmptyContent(
            onScanClick = onScanClick,
            onOpenSettings = onOpenSettings,
            modifier = modifier
        )

        is HomeUiState.Content -> PlaceholderContent(
            title = stringResource(R.string.home_content_title),
            body = stringResource(R.string.home_content_body),
            modifier = modifier
        )

        is HomeUiState.Error -> PlaceholderContent(
            title = stringResource(R.string.home_error_title),
            body = state.message,
            modifier = modifier
        )
    }
}

@Composable
private fun HomeEmptyContent(
    onScanClick: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.home_empty_title),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.home_empty_body),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onScanClick,
                modifier = Modifier.heightIn(min = 48.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(R.string.home_scan_cta))
            }
            Spacer(modifier = Modifier.height(24.dp))
            HomeModelStatusRow(onClick = onOpenSettings)
        }
    }
}

/**
 * Truthful status row: no model download flow exists yet, so the status is
 * always "not installed". Tapping routes to Settings.
 */
@Composable
private fun HomeModelStatusRow(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.home_model_status_label),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = stringResource(R.string.home_model_status_not_installed),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(name = "Home - Empty", showBackground = true)
@Composable
private fun HomeEmptyPreview() {
    KolasTheme {
        HomeScreen(state = HomeUiState.Empty, onScanClick = {}, onOpenSettings = {})
    }
}

@Preview(name = "Home - Content", showBackground = true)
@Composable
private fun HomeContentPreview() {
    KolasTheme {
        HomeScreen(
            state = HomeUiState.Content(
                recent = listOf(
                    HistoryItem(id = "1", emotion = "happy", summary = "Tail up, ears relaxed.")
                )
            ),
            onScanClick = {},
            onOpenSettings = {}
        )
    }
}

@Preview(name = "Home - Error", showBackground = true)
@Composable
private fun HomeErrorPreview() {
    KolasTheme {
        HomeScreen(
            state = HomeUiState.Error(message = stringResource(R.string.home_error_title)),
            onScanClick = {},
            onOpenSettings = {}
        )
    }
}
