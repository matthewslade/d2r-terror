package com.d2rterror.worker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.d2rterror.data.local.PreferencesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class BootReceiver : BroadcastReceiver(), KoinComponent {

    private val workerScheduler: WorkerScheduler by inject()
    private val preferencesManager: PreferencesManager by inject()

    companion object {
        private const val TAG = "BootReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d(TAG, "Boot completed, checking if notifications enabled")

            // Reschedule the worker if notifications are enabled
            CoroutineScope(Dispatchers.IO).launch {
                val notificationsEnabled = preferencesManager.notificationsEnabled.first()
                if (notificationsEnabled) {
                    Log.d(TAG, "Notifications enabled, running immediate worker to reschedule alarms")

                    // Run immediate one-time worker to fetch zones and reschedule alarms
                    val immediateWork = OneTimeWorkRequestBuilder<TerrorZoneWorker>()
                        .addTag("boot_immediate")
                        .build()
                    WorkManager.getInstance(context).enqueue(immediateWork)

                    // Also schedule the regular recurring check
                    workerScheduler.scheduleZoneCheck()
                }
            }
        }
    }
}
