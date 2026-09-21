package com.wendev.kolas.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

/**
 * Persisted user settings backed by a [DataStore].
 *
 * Reads degrade gracefully: if the store cannot be read, the flow falls back to
 * the default value instead of propagating the failure to the UI.
 */
class SettingsPreferences(
    private val dataStore: DataStore<Preferences>
) {

    val themeMode: Flow<ThemeMode> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { preferences -> ThemeMode.fromStorage(preferences[ThemeModeKey]) }

    suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { preferences -> preferences[ThemeModeKey] = mode.name }
    }

    private companion object {
        val ThemeModeKey = stringPreferencesKey("theme_mode")
    }
}
