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
    val selectedZonesCount: Int = 0
)

class SettingsViewModel(
    private val preferencesManager: PreferencesManager,
    private val workerScheduler: WorkerScheduler
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        preferencesManager.notificationsEnabled,
        preferencesManager.advanceNotificationMinutes,
        preferencesManager.selectedZones
    ) { notificationsEnabled, advanceMinutes, selectedZones ->
        SettingsUiState(
            notificationsEnabled = notificationsEnabled,
            advanceNotificationMinutes = advanceMinutes,
            selectedZonesCount = selectedZones.size
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
                workerScheduler.scheduleZoneCheck()
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
                workerScheduler.scheduleZoneCheck()
            }
        }
    }
}
