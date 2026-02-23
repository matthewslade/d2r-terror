package com.d2rterror.worker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
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

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Reschedule the worker if notifications are enabled
            CoroutineScope(Dispatchers.IO).launch {
                val notificationsEnabled = preferencesManager.notificationsEnabled.first()
                if (notificationsEnabled) {
                    workerScheduler.scheduleZoneCheck()
                }
            }
        }
    }
}
