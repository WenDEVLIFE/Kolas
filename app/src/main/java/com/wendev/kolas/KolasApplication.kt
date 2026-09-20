package com.wendev.kolas

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/** Application entry point. Hilt builds the dependency graph off this class. */
@HiltAndroidApp
class KolasApplication : Application()
