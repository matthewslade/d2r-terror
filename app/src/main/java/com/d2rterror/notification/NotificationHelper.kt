package com.d2rterror.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.d2rterror.MainActivity
import com.d2rterror.R
import com.d2rterror.data.api.ScrapedZone
import com.d2rterror.data.local.ZoneData
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "terror_zone_alerts"
        const val NOTIFICATION_ID = 1001
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.notification_channel_name)
            val description = context.getString(R.string.notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH

            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                this.description = description
                enableVibration(true)
                enableLights(true)
            }

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showZoneNotification(zones: List<ScrapedZone>, minutesUntilActive: Int) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val zoneNames = zones.joinToString(", ") { it.displayName }

        // Get act info from matched zone IDs
        val acts = zones.flatMap { zone ->
            zone.matchedIds.mapNotNull { id ->
                ZoneData.getZoneById(id)?.act
            }
        }.distinct().sorted()

        val actInfo = acts.map { act ->
            when (act) {
                1 -> "Act I"
                2 -> "Act II"
                3 -> "Act III"
                4 -> "Act IV"
                5 -> "Act V"
                else -> ""
            }
        }.filter { it.isNotEmpty() }.joinToString(", ")

        val title = context.getString(R.string.notification_title)
        val content = "$zoneNames ($actInfo)"

        // Calculate start time
        val startTime = Calendar.getInstance().apply {
            add(Calendar.MINUTE, minutesUntilActive)
        }
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val subText = context.getString(R.string.time_until_active, minutesUntilActive, timeFormat.format(startTime.time))

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(content)
            .setSubText(subText)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("$content\n$subText")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
        } catch (e: SecurityException) {
            // Permission not granted, ignore
        }
    }

    fun cancelNotification() {
        NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID)
    }

    /**
     * Show notification from an alarm trigger with pre-formatted zone data.
     *
     * @param zoneNames Comma-separated zone display names
     * @param zoneActs Comma-separated act numbers
     * @param minutesUntilActive Minutes until zone becomes active
     * @param notificationId Unique notification ID (for multiple concurrent notifications)
     */
    fun showZoneNotificationFromAlarm(
        zoneNames: String,
        zoneActs: String,
        minutesUntilActive: Int,
        notificationId: Int = NOTIFICATION_ID
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Format act info
        val actInfo = zoneActs.split(",")
            .filter { it.isNotBlank() }
            .mapNotNull { it.trim().toIntOrNull() }
            .distinct()
            .sorted()
            .map { act ->
                when (act) {
                    1 -> "Act I"
                    2 -> "Act II"
                    3 -> "Act III"
                    4 -> "Act IV"
                    5 -> "Act V"
                    else -> ""
                }
            }
            .filter { it.isNotEmpty() }
            .joinToString(", ")

        val title = context.getString(R.string.notification_title)
        val content = if (actInfo.isNotEmpty()) "$zoneNames ($actInfo)" else zoneNames

        // Calculate start time
        val startTime = Calendar.getInstance().apply {
            add(Calendar.MINUTE, minutesUntilActive)
        }
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val subText = context.getString(R.string.time_until_active, minutesUntilActive, timeFormat.format(startTime.time))

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(content)
            .setSubText(subText)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("$content\n$subText")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(notificationId, notification)
        } catch (e: SecurityException) {
            // Permission not granted, ignore
        }
    }

    fun cancelNotification(notificationId: Int) {
        NotificationManagerCompat.from(context).cancel(notificationId)
    }
}
