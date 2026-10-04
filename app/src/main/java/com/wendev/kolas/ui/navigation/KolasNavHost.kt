package com.wendev.kolas.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.wendev.kolas.ui.chat.ChatRoute
import com.wendev.kolas.ui.main.MainRoute
import com.wendev.kolas.ui.result.ResultRoute
import com.wendev.kolas.ui.settings.AboutRoute
import com.wendev.kolas.ui.settings.ModelsRoute
import com.wendev.kolas.ui.settings.SettingsRoute
import com.wendev.kolas.ui.settings.ThemeRoute
import com.wendev.kolas.ui.settings.TermsRoute
import com.wendev.kolas.ui.splash.SplashRoute

@Composable
fun KolasNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = SplashDestination,
        modifier = modifier
    ) {
        composable<SplashDestination> {
            SplashRoute(
                onFinished = {
                    navController.navigate(MainDestination) {
                        // Drop Splash so back from Main exits the app.
                        popUpTo(SplashDestination) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable<MainDestination> {
            MainRoute(
                onOpenSettings = { navController.navigate(SettingsDestination) },
                onOpenResult = { detectionId ->
                    navController.navigate(ResultDestination(detectionId))
                }
            )
        }

        composable<SettingsDestination> {
            SettingsRoute(
                onBack = { navController.popBackStack() },
                onOpenTheme = { navController.navigate(ThemeDestination) },
                onOpenModels = { navController.navigate(ModelsDestination) },
                onOpenAbout = { navController.navigate(AboutDestination) },
                onOpenTerms = { navController.navigate(TermsDestination) }
            )
        }

        composable<ThemeDestination> {
            ThemeRoute(onBack = { navController.popBackStack() })
        }

        composable<ModelsDestination> {
            ModelsRoute(onBack = { navController.popBackStack() })
        }

        composable<AboutDestination> {
            AboutRoute(onBack = { navController.popBackStack() })
        }

        composable<TermsDestination> {
            TermsRoute(onBack = { navController.popBackStack() })
        }

        composable<ResultDestination> {
            ResultRoute(
                onBack = { navController.popBackStack() },
                onScanAgain = {
                    navController.popBackStack(MainDestination, inclusive = false)
                },
                onOpenChat = { detectionId ->
                    navController.navigate(ChatDestination(detectionId))
                }
            )
        }

        composable<ChatDestination> { backStackEntry ->
            val route = backStackEntry.toRoute<ChatDestination>()
            ChatRoute(
                detectionId = route.detectionId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
