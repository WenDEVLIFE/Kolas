package com.wendev.kolas.di

import android.content.Context
import androidx.room.Room
import com.wendev.kolas.data.detection.local.DetectionDao
import com.wendev.kolas.data.detection.local.KolasDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideKolasDatabase(@ApplicationContext context: Context): KolasDatabase =
        Room.databaseBuilder(context, KolasDatabase::class.java, "kolas.db").build()

    @Provides
    fun provideDetectionDao(database: KolasDatabase): DetectionDao = database.detectionDao()
}
