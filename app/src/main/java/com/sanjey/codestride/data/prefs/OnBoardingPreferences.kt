package com.sanjey.codestride.data.prefs


import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val PREF_NAME = "codestide_prefs"

val Context.dataStore by preferencesDataStore(name = PREF_NAME)

object OnboardingPreferences {
    private val ONBOARDING_KEY = booleanPreferencesKey("has_seen_onboarding")

    fun readOnboardingSeen(context: Context): Flow<Boolean> {
        return context.dataStore.data.map { prefs ->
            prefs[ONBOARDING_KEY] ?: false
        }
    }

    suspend fun setOnboardingSeen(context: Context, seen: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[ONBOARDING_KEY] = seen
        }
    }
}
