package com.wendev.kolas.di

import com.wendev.kolas.data.detection.DetectionRepository
import com.wendev.kolas.data.detection.InMemoryDetectionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Binds the detection storage boundary to its current implementation. Swapping
 * in a Room-backed repository for Slice 4 is a one-line change here.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class DetectionModule {

    @Binds
    @Singleton
    abstract fun bindDetectionRepository(
        impl: InMemoryDetectionRepository
    ): DetectionRepository
}
