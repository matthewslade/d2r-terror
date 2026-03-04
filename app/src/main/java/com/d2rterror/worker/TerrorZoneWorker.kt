package com.d2rterror.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.d2rterror.data.local.PreferencesManager
import com.d2rterror.data.repository.TerrorZoneRepository
import com.d2rterror.notification.AlarmScheduler
import android.util.Log
import kotlinx.coroutines.flow.first
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TerrorZoneWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val repository: TerrorZoneRepository by inject()
    private val preferencesManager: PreferencesManager by inject()
    private val alarmScheduler: AlarmScheduler by inject()
    private val workerScheduler: WorkerScheduler by inject()

    override suspend fun doWork(): Result {
        Log.d(TAG, "=== TerrorZoneWorker started ===")

        // Check if notifications are enabled first
        val notificationsEnabled = preferencesManager.notificationsEnabled.first()
        Log.d(TAG, "Notifications enabled: $notificationsEnabled")

        // If disabled, don't do anything and don't reschedule
        if (!notificationsEnabled) {
            Log.d(TAG, "Notifications disabled, exiting")
            return Result.success()
        }

        return try {
            // Get selected zones
            val selectedZoneIds = preferencesManager.selectedZones.first()
            Log.d(TAG, "Selected zone IDs: $selectedZoneIds")

            // If no zones selected, still reschedule but don't fetch API
            if (selectedZoneIds.isEmpty()) {
                Log.d(TAG, "No zones selected, rescheduling")
                workerScheduler.scheduleZoneCheck(forceReplace = true)
                return Result.success()
            }

            // Refresh terror zones from API (updates the repository StateFlow)
            Log.d(TAG, "Refreshing terror zones from API...")
            val success = repository.refresh()

            if (success) {
                val state = repository.terrorZoneState.value
                Log.d(TAG, "API Success - Current zones: ${state.current.map { "${it.displayName} (IDs: ${it.matchedIds})" }}")
                Log.d(TAG, "API Success - Next zones: ${state.next.map { "${it.displayName} (IDs: ${it.matchedIds})" }}")

                // Check if any next zones match selected zones
                val matchingZones = state.next.filter { scrapedZone ->
                    val matches = scrapedZone.matchedIds.any { it in selectedZoneIds }
                    Log.d(TAG, "Zone '${scrapedZone.displayName}' matchedIds=${scrapedZone.matchedIds} matches selected=$matches")
                    matches
                }

                Log.d(TAG, "Matching zones count: ${matchingZones.size}")

                if (matchingZones.isNotEmpty()) {
                    // Get notification times from preferences
                    val notificationTimes = preferencesManager.notificationTimes.first()
                    Log.d(TAG, "Scheduling alarms for notification times: $notificationTimes")

                    // Schedule alarms for each notification time
                    alarmScheduler.scheduleNotificationAlarms(matchingZones, notificationTimes)
                } else {
                    Log.d(TAG, "No matching zones found, cancelling any pending alarms")
                    alarmScheduler.cancelAllAlarms()
                }
            } else {
                Log.e(TAG, "API Error: ${repository.terrorZoneState.value.error}")
            }

            // Schedule the next check
            workerScheduler.scheduleZoneCheck(forceReplace = true)
            Log.d(TAG, "=== TerrorZoneWorker completed, next check scheduled ===")

            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Worker exception: ${e.message}", e)
            // Only reschedule on failure if notifications are still enabled
            val stillEnabled = preferencesManager.notificationsEnabled.first()
            if (stillEnabled) {
                workerScheduler.scheduleZoneCheck(forceReplace = true)
            }
            Result.retry()
        }
    }

    companion object {
        private const val TAG = "TerrorZoneWorker"
        const val WORK_NAME = "terror_zone_check"
    }
}
