package com.d2rterror.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.d2rterror.data.local.PreferencesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.Calendar

class NotificationAlarmReceiver : BroadcastReceiver(), KoinComponent {

    private val notificationHelper: NotificationHelper by inject()
    private val preferencesManager: PreferencesManager by inject()

    companion object {
        private const val TAG = "NotificationAlarmReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val zoneNames = intent.getStringExtra(AlarmScheduler.EXTRA_ZONE_NAMES) ?: return
        val zoneActs = intent.getStringExtra(AlarmScheduler.EXTRA_ZONE_ACTS) ?: ""
        val minutesBefore = intent.getIntExtra(AlarmScheduler.EXTRA_MINUTES_BEFORE, 5)
        val notificationId = intent.getIntExtra(AlarmScheduler.EXTRA_NOTIFICATION_ID, NotificationHelper.NOTIFICATION_ID)

        Log.d(TAG, "Alarm received: zones=$zoneNames, acts=$zoneActs, minutesBefore=$minutesBefore")

        // Use a coroutine to check quiet hours (requires suspend functions)
        CoroutineScope(Dispatchers.IO).launch {
            // Check quiet hours before showing notification
            val quietHoursEnabled = preferencesManager.quietHoursEnabled.first()

            if (quietHoursEnabled) {
                val quietStart = preferencesManager.quietHoursStart.first()
                val quietEnd = preferencesManager.quietHoursEnd.first()
                val calendar = Calendar.getInstance()
                val currentMinutes = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE)

                val isInQuietHours = preferencesManager.isInQuietHours(currentMinutes, quietStart, quietEnd)

                if (isInQuietHours) {
                    Log.d(TAG, "In quiet hours, skipping notification")
                    return@launch
                }
            }

            // Show the notification
            Log.d(TAG, "Showing notification for: $zoneNames")
            notificationHelper.showZoneNotificationFromAlarm(
                zoneNames = zoneNames,
                zoneActs = zoneActs,
                minutesUntilActive = minutesBefore,
                notificationId = notificationId
            )
        }
    }
}
