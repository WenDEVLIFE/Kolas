package com.wendev.kolas

import android.app.Application
import com.wendev.kolas.data.llm.LlmEngineInitializer
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/** Application entry point. Hilt builds the dependency graph off this class. */
@HiltAndroidApp
class KolasApplication : Application() {

    /** Injected so the on-device engine warms up as soon as a model is present. */
    @Inject
    lateinit var llmEngineInitializer: LlmEngineInitializer
}
