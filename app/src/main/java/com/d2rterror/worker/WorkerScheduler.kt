package com.d2rterror.worker

import android.content.Context
import android.util.Log
import androidx.work.*
import com.d2rterror.data.local.PreferencesManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

class WorkerScheduler(
    private val context: Context,
    private val preferencesManager: PreferencesManager
) {

    private val workManager = WorkManager.getInstance(context)

    companion object {
        private const val TAG = "WorkerScheduler"
    }

    /**
     * Schedule the next terror zone check based on user's advance notification preference.
     *
     * For example, if user wants 5 minutes advance notice:
     * - Zone changes at :30 → check at :25
     * - Zone changes at :00 → check at :55 (of previous hour)
     *
     * @param forceReplace If true, replaces any existing work (use when settings change).
     *                     If false, uses KEEP policy to not disrupt existing/running work.
     */
    fun scheduleZoneCheck(forceReplace: Boolean = false) {
        val advanceMinutes = runBlocking {
            preferencesManager.advanceNotificationMinutes.first()
        }

        val now = LocalDateTime.now()
        val nextCheckTime = calculateNextCheckTime(now, advanceMinutes)
        val delayMillis = Duration.between(now, nextCheckTime).toMillis()

        // Ensure minimum delay of 1 minute
        val actualDelay = maxOf(delayMillis, 60_000L)

        val policy = if (forceReplace) ExistingWorkPolicy.REPLACE else ExistingWorkPolicy.KEEP
        Log.d(TAG, "Scheduling zone check in ${actualDelay/1000}s (at $nextCheckTime) with policy=$policy")

        val workRequest = OneTimeWorkRequestBuilder<TerrorZoneWorker>()
            .setInitialDelay(actualDelay, TimeUnit.MILLISECONDS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .addTag(TerrorZoneWorker.WORK_NAME)
            .build()

        workManager.enqueueUniqueWork(
            TerrorZoneWorker.WORK_NAME,
            policy,
            workRequest
        )
    }

    /**
     * Calculate the next time we should check for terror zones.
     *
     * @param now Current time
     * @param advanceMinutes How many minutes before zone change to notify
     * @return The next check time
     */
    private fun calculateNextCheckTime(now: LocalDateTime, advanceMinutes: Int): LocalDateTime {
        val minute = now.minute
        val second = now.second

        // Zone changes happen at :00 and :30
        // We want to check at (30 - advanceMinutes) and (60 - advanceMinutes)

        val checkMinuteForHalfHour = 30 - advanceMinutes  // e.g., 25 for 5 min advance
        val checkMinuteForHour = 60 - advanceMinutes      // e.g., 55 for 5 min advance

        return when {
            // If we're before the first check time (for :30 change)
            minute < checkMinuteForHalfHour -> {
                now.withMinute(checkMinuteForHalfHour).withSecond(0).withNano(0)
            }
            // If we're between the two check times
            minute < checkMinuteForHour -> {
                now.withMinute(checkMinuteForHour).withSecond(0).withNano(0)
            }
            // We're past both check times, schedule for next hour's first check
            else -> {
                now.plusHours(1).withMinute(checkMinuteForHalfHour).withSecond(0).withNano(0)
            }
        }
    }

    /**
     * Cancel any scheduled zone checks
     */
    fun cancelZoneCheck() {
        workManager.cancelUniqueWork(TerrorZoneWorker.WORK_NAME)
    }

    /**
     * Check if there's a pending zone check scheduled
     */
    fun isZoneCheckScheduled(): Boolean {
        val workInfo = workManager.getWorkInfosForUniqueWork(TerrorZoneWorker.WORK_NAME)
        return try {
            val infos = workInfo.get()
            infos.any { it.state == WorkInfo.State.ENQUEUED || it.state == WorkInfo.State.RUNNING }
        } catch (e: Exception) {
            false
        }
    }
}
