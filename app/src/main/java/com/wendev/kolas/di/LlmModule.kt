package com.wendev.kolas.di

import com.wendev.kolas.data.llm.LlamaCppInference
import com.wendev.kolas.data.llm.LlamaInference
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Binds the chat engine to the on-device llama.cpp JNI implementation.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class LlmModule {

    @Binds
    @Singleton
    abstract fun bindLlamaInference(impl: LlamaCppInference): LlamaInference
}
