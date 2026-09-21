package com.wendev.kolas.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.wendev.kolas.ui.history.HistoryRoute
import com.wendev.kolas.ui.home.HomeRoute
import com.wendev.kolas.ui.scan.ScanRoute

/**
 * Stateful host of the Home/Scan/History tabs.
 *
 * Tab selection is plain [rememberSaveable] state rather than a nested NavHost:
 * all tabs are graph roots with no per-tab detail hierarchy (details such as
 * Result and Chat are full-screen destinations on the root graph), so a second
 * NavController would only add indirection without buying back-stack depth.
 */
@Composable
fun MainRoute(
    onOpenSettings: () -> Unit,
    onOpenResult: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by rememberSaveable { mutableStateOf(MainTab.Home) }

    MainScreen(
        selectedTab = selectedTab,
        onTabSelected = { selectedTab = it },
        onOpenSettings = onOpenSettings,
        modifier = modifier
    ) {
        when (selectedTab) {
            MainTab.Home -> HomeRoute(
                // Cross-tab navigation: switch the selected tab, not the nav graph.
                onScanClick = { selectedTab = MainTab.Scan },
                onOpenSettings = onOpenSettings
            )

            MainTab.Scan -> ScanRoute(onCaptured = onOpenResult)
            MainTab.History -> HistoryRoute(onOpenResult = onOpenResult)
        }
    }
}
