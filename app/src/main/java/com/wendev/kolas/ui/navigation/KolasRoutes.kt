package com.wendev.kolas.ui.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe navigation destinations for Kolas.
 *
 * The naming rule: destination types carry the `Destination` suffix, while the
 * stateful composables keep the `Route` suffix (e.g. [SplashDestination] type vs
 * `SplashRoute` composable). This keeps the two concepts from colliding.
 */
@Serializable
data object SplashDestination

/** Bottom-nav host: the Scan and History tabs live inside it. */
@Serializable
data object MainDestination

/** Bottom-nav tab. */
@Serializable
data object ScanDestination

/** Bottom-nav tab. */
@Serializable
data object HistoryDestination

/** Pushed full-screen. */
@Serializable
data object SettingsDestination

/** Pushed full-screen, carrying the detection it explains. */
@Serializable
data class ResultDestination(val detectionId: String)

/** Pushed full-screen, carrying the detection the chat is about. */
@Serializable
data class ChatDestination(val detectionId: String)
