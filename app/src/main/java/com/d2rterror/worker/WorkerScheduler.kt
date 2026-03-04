package com.d2rterror.worker

import android.content.Context
import android.util.Log
import androidx.work.*
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

class WorkerScheduler(
    private val context: Context
) {

    private val workManager = WorkManager.getInstance(context)

    companion object {
        private const val TAG = "WorkerScheduler"
        // Check at :01 and :31 (1 minute after zone change for API freshness)
        private const val CHECK_MINUTE_FIRST = 1   // For :00 zone change
        private const val CHECK_MINUTE_SECOND = 31 // For :30 zone change
    }

    /**
     * Schedule the next terror zone check at fixed times (:01 and :31).
     * WorkManager fetches fresh zone data, then AlarmScheduler handles exact notification timing.
     *
     * @param forceReplace If true, replaces any existing work (use when settings change).
     *                     If false, uses KEEP policy to not disrupt existing/running work.
     */
    fun scheduleZoneCheck(forceReplace: Boolean = false) {
        val now = LocalDateTime.now()
        val nextCheckTime = calculateNextCheckTime(now)
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
     * Always targets :01 and :31 (1 minute after zone changes).
     *
     * @param now Current time
     * @return The next check time
     */
    private fun calculateNextCheckTime(now: LocalDateTime): LocalDateTime {
        val minute = now.minute

        return when {
            // Before :01 - schedule for :01
            minute < CHECK_MINUTE_FIRST -> {
                now.withMinute(CHECK_MINUTE_FIRST).withSecond(0).withNano(0)
            }
            // Between :01 and :31 - schedule for :31
            minute < CHECK_MINUTE_SECOND -> {
                now.withMinute(CHECK_MINUTE_SECOND).withSecond(0).withNano(0)
            }
            // After :31 - schedule for next hour's :01
            else -> {
                now.plusHours(1).withMinute(CHECK_MINUTE_FIRST).withSecond(0).withNano(0)
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
