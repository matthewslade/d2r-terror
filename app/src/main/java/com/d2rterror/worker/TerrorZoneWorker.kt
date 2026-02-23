package com.d2rterror.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.d2rterror.data.local.PreferencesManager
import com.d2rterror.data.repository.TerrorZoneRepository
import com.d2rterror.notification.NotificationHelper
import android.util.Log
import com.d2rterror.ui.components.getMinutesUntilNextChange
import kotlinx.coroutines.flow.first
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.Calendar

class TerrorZoneWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val repository: TerrorZoneRepository by inject()
    private val preferencesManager: PreferencesManager by inject()
    private val notificationHelper: NotificationHelper by inject()
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
                workerScheduler.scheduleZoneCheck()
                return Result.success()
            }

            // Fetch terror zones from API
            Log.d(TAG, "Fetching terror zones from API...")
            val result = repository.getTerrorZones()

            result.onSuccess { state ->
                Log.d(TAG, "API Success - Current zones: ${state.current.map { "${it.displayName} (IDs: ${it.matchedIds})" }}")
                Log.d(TAG, "API Success - Next zones: ${state.next.map { "${it.displayName} (IDs: ${it.matchedIds})" }}")

                // Check if any next zones match selected zones
                // A ScrapedZone matches if any of its matchedIds are in selectedZoneIds
                val matchingZones = state.next.filter { scrapedZone ->
                    val matches = scrapedZone.matchedIds.any { it in selectedZoneIds }
                    Log.d(TAG, "Zone '${scrapedZone.displayName}' matchedIds=${scrapedZone.matchedIds} matches selected=$matches")
                    matches
                }

                Log.d(TAG, "Matching zones count: ${matchingZones.size}")

                if (matchingZones.isNotEmpty()) {
                    // Check quiet hours before notifying
                    val quietHoursEnabled = preferencesManager.quietHoursEnabled.first()
                    val quietStart = preferencesManager.quietHoursStart.first()
                    val quietEnd = preferencesManager.quietHoursEnd.first()
                    val calendar = Calendar.getInstance()
                    val currentMinutes = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE)

                    Log.d(TAG, "Quiet hours enabled: $quietHoursEnabled, start: $quietStart, end: $quietEnd, current: $currentMinutes")

                    val isInQuietHours = if (quietHoursEnabled) {
                        preferencesManager.isInQuietHours(currentMinutes, quietStart, quietEnd)
                    } else {
                        false
                    }

                    Log.d(TAG, "Is in quiet hours: $isInQuietHours")

                    // Skip notification if in quiet hours
                    if (isInQuietHours) {
                        Log.d(TAG, "In quiet hours, skipping notification but rescheduling")
                        workerScheduler.scheduleZoneCheck()
                        return@onSuccess
                    }

                    // Create a unique key for this zone set to prevent duplicate notifications
                    val allMatchedIds = matchingZones.flatMap { it.matchedIds }.sorted()
                    val zoneKey = allMatchedIds.joinToString(",")
                    val lastNotifiedZone = preferencesManager.lastNotifiedZone.first()
                    val lastNotificationTime = preferencesManager.lastNotificationTime.first()

                    // Only notify if we haven't already notified for this exact zone set recently
                    // (within the last 25 minutes to account for the 30-minute cycle)
                    val currentTime = System.currentTimeMillis()
                    val timeSinceLastNotification = currentTime - lastNotificationTime
                    val twentyFiveMinutesMs = 25 * 60 * 1000L

                    Log.d(TAG, "Zone key: $zoneKey, last notified: $lastNotifiedZone, time since last: ${timeSinceLastNotification/1000}s")

                    if (zoneKey != lastNotifiedZone || timeSinceLastNotification > twentyFiveMinutesMs) {
                        val minutesUntilActive = getMinutesUntilNextChange()
                        Log.d(TAG, "SENDING NOTIFICATION for ${matchingZones.map { it.displayName }}, minutes until active: $minutesUntilActive")
                        notificationHelper.showZoneNotification(matchingZones, minutesUntilActive)
                        preferencesManager.setLastNotification(zoneKey, currentTime)
                    } else {
                        Log.d(TAG, "Skipping duplicate notification")
                    }
                } else {
                    Log.d(TAG, "No matching zones found")
                }
            }

            result.onFailure { error ->
                Log.e(TAG, "API Error: ${error.message}", error)
            }

            // Schedule the next check
            workerScheduler.scheduleZoneCheck()
            Log.d(TAG, "=== TerrorZoneWorker completed, next check scheduled ===")

            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Worker exception: ${e.message}", e)
            // Only reschedule on failure if notifications are still enabled
            val stillEnabled = preferencesManager.notificationsEnabled.first()
            if (stillEnabled) {
                workerScheduler.scheduleZoneCheck()
            }
            Result.retry()
        }
    }

    companion object {
        private const val TAG = "TerrorZoneWorker"
        const val WORK_NAME = "terror_zone_check"
    }
}
