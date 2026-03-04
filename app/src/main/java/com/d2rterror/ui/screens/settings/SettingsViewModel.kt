package com.d2rterror.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d2rterror.data.local.PreferencesManager
import com.d2rterror.worker.WorkerScheduler
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SettingsUiState(
    val notificationsEnabled: Boolean = true,
    val advanceNotificationMinutes: Int = PreferencesManager.DEFAULT_ADVANCE_MINUTES,
    val notificationTimes: Set<Int> = setOf(PreferencesManager.DEFAULT_ADVANCE_MINUTES),
    val selectedZonesCount: Int = 0,
    val quietHoursEnabled: Boolean = false,
    val quietHoursStart: Int = PreferencesManager.DEFAULT_QUIET_START,
    val quietHoursEnd: Int = PreferencesManager.DEFAULT_QUIET_END
)

class SettingsViewModel(
    private val preferencesManager: PreferencesManager,
    private val workerScheduler: WorkerScheduler
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        preferencesManager.notificationsEnabled,
        preferencesManager.advanceNotificationMinutes,
        preferencesManager.notificationTimes,
        preferencesManager.selectedZones,
        preferencesManager.quietHoursEnabled,
        preferencesManager.quietHoursStart,
        preferencesManager.quietHoursEnd
    ) { values ->
        @Suppress("UNCHECKED_CAST")
        val notificationsEnabled = values[0] as Boolean
        val advanceMinutes = values[1] as Int
        val notificationTimes = values[2] as Set<Int>
        val selectedZones = values[3] as Set<Int>
        val quietHoursEnabled = values[4] as Boolean
        val quietHoursStart = values[5] as Int
        val quietHoursEnd = values[6] as Int

        SettingsUiState(
            notificationsEnabled = notificationsEnabled,
            advanceNotificationMinutes = advanceMinutes,
            notificationTimes = notificationTimes,
            selectedZonesCount = selectedZones.size,
            quietHoursEnabled = quietHoursEnabled,
            quietHoursStart = quietHoursStart,
            quietHoursEnd = quietHoursEnd
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setNotificationsEnabled(enabled)
            if (enabled) {
                workerScheduler.scheduleZoneCheck(forceReplace = true)
            } else {
                workerScheduler.cancelZoneCheck()
            }
        }
    }

    fun setAdvanceNotificationMinutes(minutes: Int) {
        viewModelScope.launch {
            preferencesManager.setAdvanceNotificationMinutes(minutes)
            // Reschedule worker with new timing
            if (uiState.value.notificationsEnabled) {
                workerScheduler.scheduleZoneCheck(forceReplace = true)
            }
        }
    }

    fun addNotificationTime(minutes: Int) {
        viewModelScope.launch {
            preferencesManager.addNotificationTime(minutes)
        }
    }

    fun removeNotificationTime(minutes: Int) {
        viewModelScope.launch {
            preferencesManager.removeNotificationTime(minutes)
        }
    }

    /**
     * Save notification times and clear last notification to allow re-testing.
     * Called after debounce when user modifies notification times.
     */
    fun saveNotificationTimes(times: Set<Int>) {
        viewModelScope.launch {
            preferencesManager.setNotificationTimes(times)
            preferencesManager.clearLastNotification()
        }
    }

    fun setQuietHoursEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setQuietHoursEnabled(enabled)
        }
    }

    fun setQuietHoursStart(minutesFromMidnight: Int) {
        viewModelScope.launch {
            preferencesManager.setQuietHoursStart(minutesFromMidnight)
        }
    }

    fun setQuietHoursEnd(minutesFromMidnight: Int) {
        viewModelScope.launch {
            preferencesManager.setQuietHoursEnd(minutesFromMidnight)
        }
    }
}
