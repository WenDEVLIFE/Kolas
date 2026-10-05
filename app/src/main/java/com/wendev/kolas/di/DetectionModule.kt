package com.wendev.kolas.di

import com.wendev.kolas.data.detection.DetectionRepository
import com.wendev.kolas.data.detection.RoomDetectionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Binds the detection storage boundary to its Room (SQLite) implementation.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class DetectionModule {

    @Binds
    @Singleton
    abstract fun bindDetectionRepository(
        impl: RoomDetectionRepository
    ): DetectionRepository
}
