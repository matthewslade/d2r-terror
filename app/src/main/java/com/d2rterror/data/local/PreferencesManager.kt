package com.d2rterror.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "d2r_terror_prefs")

class PreferencesManager(private val context: Context) {

    companion object {
        private val SELECTED_ZONES = stringSetPreferencesKey("selected_zones")
        private val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        private val ADVANCE_NOTIFICATION_MINUTES = intPreferencesKey("advance_notification_minutes")
        private val LAST_NOTIFIED_ZONE = stringPreferencesKey("last_notified_zone")
        private val LAST_NOTIFICATION_TIME = longPreferencesKey("last_notification_time")

        const val DEFAULT_ADVANCE_MINUTES = 5
        const val MIN_ADVANCE_MINUTES = 1
        const val MAX_ADVANCE_MINUTES = 30
    }

    // Selected zone IDs
    val selectedZones: Flow<Set<Int>> = context.dataStore.data
        .map { preferences ->
            preferences[SELECTED_ZONES]
                ?.mapNotNull { it.toIntOrNull() }
                ?.toSet()
                ?: emptySet()
        }

    suspend fun setSelectedZones(zones: Set<Int>) {
        context.dataStore.edit { preferences ->
            preferences[SELECTED_ZONES] = zones.map { it.toString() }.toSet()
        }
    }

    suspend fun addSelectedZone(zoneId: Int) {
        context.dataStore.edit { preferences ->
            val current = preferences[SELECTED_ZONES]
                ?.mapNotNull { it.toIntOrNull() }
                ?.toMutableSet()
                ?: mutableSetOf()
            current.add(zoneId)
            preferences[SELECTED_ZONES] = current.map { it.toString() }.toSet()
        }
    }

    suspend fun removeSelectedZone(zoneId: Int) {
        context.dataStore.edit { preferences ->
            val current = preferences[SELECTED_ZONES]
                ?.mapNotNull { it.toIntOrNull() }
                ?.toMutableSet()
                ?: mutableSetOf()
            current.remove(zoneId)
            preferences[SELECTED_ZONES] = current.map { it.toString() }.toSet()
        }
    }

    // Notifications enabled
    val notificationsEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[NOTIFICATIONS_ENABLED] ?: true
        }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFICATIONS_ENABLED] = enabled
        }
    }

    // Advance notification time (1-30 minutes)
    val advanceNotificationMinutes: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[ADVANCE_NOTIFICATION_MINUTES] ?: DEFAULT_ADVANCE_MINUTES
        }

    suspend fun setAdvanceNotificationMinutes(minutes: Int) {
        val clampedMinutes = minutes.coerceIn(MIN_ADVANCE_MINUTES, MAX_ADVANCE_MINUTES)
        context.dataStore.edit { preferences ->
            preferences[ADVANCE_NOTIFICATION_MINUTES] = clampedMinutes
        }
    }

    // Last notified zone (to prevent duplicate notifications)
    val lastNotifiedZone: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[LAST_NOTIFIED_ZONE]
        }

    val lastNotificationTime: Flow<Long> = context.dataStore.data
        .map { preferences ->
            preferences[LAST_NOTIFICATION_TIME] ?: 0L
        }

    suspend fun setLastNotification(zoneKey: String, time: Long) {
        context.dataStore.edit { preferences ->
            preferences[LAST_NOTIFIED_ZONE] = zoneKey
            preferences[LAST_NOTIFICATION_TIME] = time
        }
    }
}
