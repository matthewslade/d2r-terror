package com.d2rterror.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.d2rterror.data.api.ScrapedZone
import java.util.Calendar

class AlarmScheduler(private val context: Context) {

    companion object {
        private const val TAG = "AlarmScheduler"
        const val EXTRA_ZONE_NAMES = "zone_names"
        const val EXTRA_ZONE_ACTS = "zone_acts"
        const val EXTRA_MINUTES_BEFORE = "minutes_before"
        const val EXTRA_NOTIFICATION_ID = "notification_id"

        // Base request code for pending intents - each notification time gets a unique code
        private const val BASE_REQUEST_CODE = 2000
    }

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    /**
     * Schedule exact alarms for each notification time before the next zone change.
     * Zone changes happen at :00 and :30.
     *
     * @param matchingZones The zones that matched user's selection
     * @param notificationTimes Set of minutes before zone change to notify (e.g., {5, 10, 15})
     */
    fun scheduleNotificationAlarms(matchingZones: List<ScrapedZone>, notificationTimes: Set<Int>) {
        // Cancel any existing alarms first
        cancelAllAlarms()

        if (matchingZones.isEmpty() || notificationTimes.isEmpty()) {
            Log.d(TAG, "No zones or times to schedule")
            return
        }

        val zoneNames = matchingZones.joinToString(", ") { it.displayName }
        val zoneActs = matchingZones.flatMap { it.matchedIds }
            .mapNotNull { id -> com.d2rterror.data.local.ZoneData.getZoneById(id)?.act }
            .distinct()
            .sorted()
            .joinToString(",")

        // Calculate next zone change time
        val nextChangeTime = calculateNextZoneChangeTime()
        Log.d(TAG, "Next zone change at: ${formatTime(nextChangeTime)}")

        // Schedule an alarm for each notification time
        notificationTimes.forEachIndexed { index, minutesBefore ->
            val alarmTime = nextChangeTime - (minutesBefore * 60 * 1000L)
            val now = System.currentTimeMillis()

            // Only schedule if alarm time is in the future
            if (alarmTime > now) {
                scheduleExactAlarm(
                    alarmTime = alarmTime,
                    zoneNames = zoneNames,
                    zoneActs = zoneActs,
                    minutesBefore = minutesBefore,
                    requestCode = BASE_REQUEST_CODE + index
                )
                Log.d(TAG, "Scheduled alarm for $minutesBefore min before (at ${formatTime(alarmTime)})")
            } else {
                Log.d(TAG, "Skipping alarm for $minutesBefore min before - time already passed")
            }
        }
    }

    /**
     * Schedule a single exact alarm.
     */
    private fun scheduleExactAlarm(
        alarmTime: Long,
        zoneNames: String,
        zoneActs: String,
        minutesBefore: Int,
        requestCode: Int
    ) {
        val intent = Intent(context, NotificationAlarmReceiver::class.java).apply {
            putExtra(EXTRA_ZONE_NAMES, zoneNames)
            putExtra(EXTRA_ZONE_ACTS, zoneActs)
            putExtra(EXTRA_MINUTES_BEFORE, minutesBefore)
            putExtra(EXTRA_NOTIFICATION_ID, requestCode)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        alarmTime,
                        pendingIntent
                    )
                } else {
                    // Fall back to inexact alarm if exact alarms not permitted
                    Log.w(TAG, "Exact alarms not permitted, using inexact")
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        alarmTime,
                        pendingIntent
                    )
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    alarmTime,
                    pendingIntent
                )
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException scheduling alarm: ${e.message}")
        }
    }

    /**
     * Cancel all scheduled notification alarms.
     */
    fun cancelAllAlarms() {
        // Cancel alarms for all possible request codes (0-29 for 1-30 minutes)
        for (i in 0 until 30) {
            val intent = Intent(context, NotificationAlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                BASE_REQUEST_CODE + i,
                intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            pendingIntent?.let {
                alarmManager.cancel(it)
                it.cancel()
            }
        }
        Log.d(TAG, "Cancelled all alarms")
    }

    /**
     * Calculate the next zone change time (next :00 or :30).
     */
    private fun calculateNextZoneChangeTime(): Long {
        val calendar = Calendar.getInstance()
        val minute = calendar.get(Calendar.MINUTE)

        return when {
            minute < 30 -> {
                // Next change is at :30
                calendar.set(Calendar.MINUTE, 30)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                calendar.timeInMillis
            }
            else -> {
                // Next change is at :00 of next hour
                calendar.add(Calendar.HOUR_OF_DAY, 1)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                calendar.timeInMillis
            }
        }
    }

    private fun formatTime(millis: Long): String {
        val calendar = Calendar.getInstance().apply { timeInMillis = millis }
        return String.format(
            "%02d:%02d:%02d",
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            calendar.get(Calendar.SECOND)
        )
    }
}
