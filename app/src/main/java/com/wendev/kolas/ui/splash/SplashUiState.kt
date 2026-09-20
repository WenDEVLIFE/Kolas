package com.wendev.kolas.ui.splash

/**
 * State of the startup screen.
 *
 * [Loading.progress] is nullable so the screen can render both a determinate
 * bar (progress in `0f..1f`) and an indeterminate one (`null`).
 */
sealed interface SplashUiState {

    data class Loading(
        val progress: Float?,
        val message: String?
    ) : SplashUiState

    data class Error(val message: String) : SplashUiState

    data object Ready : SplashUiState
}
