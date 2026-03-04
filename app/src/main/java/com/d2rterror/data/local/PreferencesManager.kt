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
        private val ADVANCE_NOTIFICATION_MINUTES = intPreferencesKey("advance_notification_minutes") // Legacy, for migration
        private val NOTIFICATION_TIMES = stringSetPreferencesKey("notification_times") // Set of minutes as strings
        private val LAST_NOTIFIED_ZONE = stringPreferencesKey("last_notified_zone")
        private val LAST_NOTIFICATION_TIME = longPreferencesKey("last_notification_time")
        private val QUIET_HOURS_ENABLED = booleanPreferencesKey("quiet_hours_enabled")
        private val QUIET_HOURS_START = intPreferencesKey("quiet_hours_start") // Minutes from midnight
        private val QUIET_HOURS_END = intPreferencesKey("quiet_hours_end")     // Minutes from midnight

        const val DEFAULT_ADVANCE_MINUTES = 5
        const val MIN_ADVANCE_MINUTES = 1
        const val MAX_ADVANCE_MINUTES = 30

        // Default quiet hours: 10 PM to 7 AM
        const val DEFAULT_QUIET_START = 22 * 60  // 10:00 PM = 1320 minutes
        const val DEFAULT_QUIET_END = 7 * 60     // 7:00 AM = 420 minutes
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

    // Advance notification time (1-30 minutes) - Legacy, kept for migration
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

    // Multiple notification times (Set of minutes before zone change)
    val notificationTimes: Flow<Set<Int>> = context.dataStore.data
        .map { preferences ->
            val times = preferences[NOTIFICATION_TIMES]
                ?.mapNotNull { it.toIntOrNull() }
                ?.toSet()

            // If no notification times exist, migrate from legacy single value or use default
            if (times.isNullOrEmpty()) {
                val legacyMinutes = preferences[ADVANCE_NOTIFICATION_MINUTES] ?: DEFAULT_ADVANCE_MINUTES
                setOf(legacyMinutes)
            } else {
                times
            }
        }

    suspend fun addNotificationTime(minutes: Int) {
        val clampedMinutes = minutes.coerceIn(MIN_ADVANCE_MINUTES, MAX_ADVANCE_MINUTES)
        context.dataStore.edit { preferences ->
            val current = preferences[NOTIFICATION_TIMES]
                ?.mapNotNull { it.toIntOrNull() }
                ?.toMutableSet()
                ?: mutableSetOf(preferences[ADVANCE_NOTIFICATION_MINUTES] ?: DEFAULT_ADVANCE_MINUTES)
            current.add(clampedMinutes)
            preferences[NOTIFICATION_TIMES] = current.map { it.toString() }.toSet()
        }
    }

    suspend fun removeNotificationTime(minutes: Int) {
        context.dataStore.edit { preferences ->
            val current = preferences[NOTIFICATION_TIMES]
                ?.mapNotNull { it.toIntOrNull() }
                ?.toMutableSet()
                ?: mutableSetOf(preferences[ADVANCE_NOTIFICATION_MINUTES] ?: DEFAULT_ADVANCE_MINUTES)

            // Ensure at least one time always exists
            if (current.size > 1) {
                current.remove(minutes)
                preferences[NOTIFICATION_TIMES] = current.map { it.toString() }.toSet()
            }
        }
    }

    suspend fun setNotificationTimes(times: Set<Int>) {
        val validTimes = times
            .map { it.coerceIn(MIN_ADVANCE_MINUTES, MAX_ADVANCE_MINUTES) }
            .toSet()
            .ifEmpty { setOf(DEFAULT_ADVANCE_MINUTES) }

        context.dataStore.edit { preferences ->
            preferences[NOTIFICATION_TIMES] = validTimes.map { it.toString() }.toSet()
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

    /**
     * Clear the last notification tracking to allow re-testing notifications.
     */
    suspend fun clearLastNotification() {
        context.dataStore.edit { preferences ->
            preferences.remove(LAST_NOTIFIED_ZONE)
            preferences[LAST_NOTIFICATION_TIME] = 0L
        }
    }

    // Quiet hours enabled
    val quietHoursEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[QUIET_HOURS_ENABLED] ?: false
        }

    suspend fun setQuietHoursEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[QUIET_HOURS_ENABLED] = enabled
        }
    }

    // Quiet hours start time (minutes from midnight, 0-1439)
    val quietHoursStart: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[QUIET_HOURS_START] ?: DEFAULT_QUIET_START
        }

    suspend fun setQuietHoursStart(minutesFromMidnight: Int) {
        context.dataStore.edit { preferences ->
            preferences[QUIET_HOURS_START] = minutesFromMidnight.coerceIn(0, 1439)
        }
    }

    // Quiet hours end time (minutes from midnight, 0-1439)
    val quietHoursEnd: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[QUIET_HOURS_END] ?: DEFAULT_QUIET_END
        }

    suspend fun setQuietHoursEnd(minutesFromMidnight: Int) {
        context.dataStore.edit { preferences ->
            preferences[QUIET_HOURS_END] = minutesFromMidnight.coerceIn(0, 1439)
        }
    }

    /**
     * Check if the given time (minutes from midnight) falls within quiet hours.
     * Handles overnight ranges (e.g., 10 PM to 7 AM).
     */
    fun isInQuietHours(currentMinutes: Int, quietStart: Int, quietEnd: Int): Boolean {
        return if (quietStart <= quietEnd) {
            // Same day range (e.g., 9 AM to 5 PM)
            currentMinutes in quietStart until quietEnd
        } else {
            // Overnight range (e.g., 10 PM to 7 AM)
            currentMinutes >= quietStart || currentMinutes < quietEnd
        }
    }
}
