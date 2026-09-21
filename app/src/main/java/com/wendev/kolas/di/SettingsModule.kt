package com.wendev.kolas.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.wendev.kolas.data.preferences.SettingsPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * The DataStore delegate must live at file scope (it is a property delegate on
 * [Context]) so the store is created exactly once per process. Declaring it
 * inside the module object would hand out a different instance per call and
 * crash with "There are multiple DataStores active for the same file".
 */
private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "kolas_settings"
)

@Module
@InstallIn(SingletonComponent::class)
object SettingsModule {

    @Provides
    @Singleton
    fun provideSettingsDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> = context.settingsDataStore

    @Provides
    @Singleton
    fun provideSettingsPreferences(
        dataStore: DataStore<Preferences>
    ): SettingsPreferences = SettingsPreferences(dataStore)
}
