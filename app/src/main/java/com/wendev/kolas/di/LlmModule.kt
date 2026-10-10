package com.wendev.kolas.di

import com.wendev.kolas.data.llm.LlamaInference
import com.wendev.kolas.data.llm.StubLlamaInference
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Binds the chat engine. Currently the stub; swap to the llama.cpp JNI engine
 * here when it lands.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class LlmModule {

    @Binds
    @Singleton
    abstract fun bindLlamaInference(impl: StubLlamaInference): LlamaInference
}
