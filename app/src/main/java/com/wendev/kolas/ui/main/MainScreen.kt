package com.wendev.kolas.ui.main

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.wendev.kolas.R
import com.wendev.kolas.ui.theme.KolasTheme

/** The top-level tabs hosted by [MainScreen], in bottom-bar order. */
enum class MainTab(
    @param:StringRes val labelRes: Int,
    val icon: ImageVector
) {
    Home(labelRes = R.string.main_tab_home, icon = Icons.Filled.Home),
    Scan(labelRes = R.string.main_tab_scan, icon = Icons.Filled.Search),
    History(labelRes = R.string.main_tab_history, icon = Icons.AutoMirrored.Filled.List)
}

@Composable
fun MainScreen(
    selectedTab: MainTab,
    onTabSelected: (MainTab) -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = { MainTopBar(onOpenSettings = onOpenSettings) },
        bottomBar = { MainBottomBar(selectedTab = selectedTab, onTabSelected = onTabSelected) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainTopBar(
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        modifier = modifier,
        title = { Text(text = stringResource(R.string.main_title)) },
        actions = {
            IconButton(onClick = onOpenSettings) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = stringResource(R.string.main_open_settings),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

@Composable
private fun MainBottomBar(
    selectedTab: MainTab,
    onTabSelected: (MainTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        MainTab.entries.forEach { tab ->
            NavigationBarItem(
                selected = tab == selectedTab,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        imageVector = tab.icon,
                        // The visible label already names the destination.
                        contentDescription = null
                    )
                },
                label = { Text(text = stringResource(tab.labelRes)) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.onSurface,
                    indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

@Preview(name = "Main - Home tab", showBackground = true)
@Composable
private fun MainHomePreview() {
    KolasTheme {
        MainScreen(
            selectedTab = MainTab.Home,
            onTabSelected = {},
            onOpenSettings = {},
            content = {}
        )
    }
}

@Preview(name = "Main - Scan tab", showBackground = true)
@Composable
private fun MainScanPreview() {
    KolasTheme {
        MainScreen(
            selectedTab = MainTab.Scan,
            onTabSelected = {},
            onOpenSettings = {},
            content = {}
        )
    }
}

@Preview(name = "Main - History tab", showBackground = true)
@Composable
private fun MainHistoryPreview() {
    KolasTheme {
        MainScreen(
            selectedTab = MainTab.History,
            onTabSelected = {},
            onOpenSettings = {},
            content = {}
        )
    }
}
