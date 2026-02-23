package com.d2rterror.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.d2rterror.data.local.PreferencesManager
import com.d2rterror.data.repository.TerrorZoneRepository
import com.d2rterror.notification.NotificationHelper
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
        // Check if notifications are enabled first
        val notificationsEnabled = preferencesManager.notificationsEnabled.first()

        // If disabled, don't do anything and don't reschedule
        if (!notificationsEnabled) {
            return Result.success()
        }

        return try {
            // Get selected zones
            val selectedZoneIds = preferencesManager.selectedZones.first()

            // If no zones selected, still reschedule but don't fetch API
            if (selectedZoneIds.isEmpty()) {
                workerScheduler.scheduleZoneCheck()
                return Result.success()
            }

            // Fetch terror zones from API
            val result = repository.getTerrorZones()

            result.onSuccess { state ->
                // Check if any next zones match selected zones
                // A ScrapedZone matches if any of its matchedIds are in selectedZoneIds
                val matchingZones = state.next.filter { scrapedZone ->
                    scrapedZone.matchedIds.any { it in selectedZoneIds }
                }

                if (matchingZones.isNotEmpty()) {
                    // Check quiet hours before notifying
                    val quietHoursEnabled = preferencesManager.quietHoursEnabled.first()
                    val isInQuietHours = if (quietHoursEnabled) {
                        val quietStart = preferencesManager.quietHoursStart.first()
                        val quietEnd = preferencesManager.quietHoursEnd.first()
                        val calendar = Calendar.getInstance()
                        val currentMinutes = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE)
                        preferencesManager.isInQuietHours(currentMinutes, quietStart, quietEnd)
                    } else {
                        false
                    }

                    // Skip notification if in quiet hours
                    if (isInQuietHours) {
                        // Still reschedule but don't notify
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

                    if (zoneKey != lastNotifiedZone || timeSinceLastNotification > twentyFiveMinutesMs) {
                        val minutesUntilActive = getMinutesUntilNextChange()
                        notificationHelper.showZoneNotification(matchingZones, minutesUntilActive)
                        preferencesManager.setLastNotification(zoneKey, currentTime)
                    }
                }
            }

            // Schedule the next check
            workerScheduler.scheduleZoneCheck()

            Result.success()
        } catch (e: Exception) {
            // Only reschedule on failure if notifications are still enabled
            val stillEnabled = preferencesManager.notificationsEnabled.first()
            if (stillEnabled) {
                workerScheduler.scheduleZoneCheck()
            }
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "terror_zone_check"
    }
}
