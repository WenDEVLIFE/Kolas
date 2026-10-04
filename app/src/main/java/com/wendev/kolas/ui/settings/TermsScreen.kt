package com.wendev.kolas.ui.settings

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
fun TermsScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    BackScaffold(
        title = stringResource(R.string.settings_item_terms),
        onBack = onBack,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TermsSection(
                titleRes = R.string.terms_agreement_title,
                bodyRes = R.string.terms_agreement_body
            )
            TermsSection(
                titleRes = R.string.terms_use_title,
                bodyRes = R.string.terms_use_body
            )
            TermsSection(
                titleRes = R.string.terms_privacy_title,
                bodyRes = R.string.terms_privacy_body
            )
            TermsSection(
                titleRes = R.string.terms_warranty_title,
                bodyRes = R.string.terms_warranty_body
            )
            TermsSection(
                titleRes = R.string.terms_changes_title,
                bodyRes = R.string.terms_changes_body
            )
        }
    }
}

@Composable
private fun TermsSection(
    @StringRes titleRes: Int,
    @StringRes bodyRes: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = stringResource(titleRes),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = stringResource(bodyRes),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(name = "Terms and Conditions", showBackground = true)
@Composable
private fun TermsPreview() {
    KolasTheme {
        TermsScreen(onBack = {})
    }
}
