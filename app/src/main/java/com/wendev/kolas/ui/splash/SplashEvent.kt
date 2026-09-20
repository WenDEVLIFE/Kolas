package com.wendev.kolas.ui.splash

/** One-off effects emitted by [SplashViewModel]. Never persisted as state. */
sealed interface SplashEvent {
    data object NavigateToHome : SplashEvent
}
