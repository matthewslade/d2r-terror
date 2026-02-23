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
        val subText = context.getString(R.string.time_until_active, minutesUntilActive)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(content)
            .setSubText(subText)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("$content\n\n$subText")
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
}
