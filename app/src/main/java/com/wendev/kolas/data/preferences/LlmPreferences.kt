package com.wendev.kolas.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

/** Persists which chat model the user selected. */
class LlmPreferences(
    private val dataStore: DataStore<Preferences>
) {

    val selectedModelId: Flow<String?> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { preferences -> preferences[SelectedModelIdKey] }

    suspend fun setSelectedModelId(id: String) {
        dataStore.edit { preferences -> preferences[SelectedModelIdKey] = id }
    }

    private companion object {
        val SelectedModelIdKey = stringPreferencesKey("selected_llm_model_id")
    }
}
