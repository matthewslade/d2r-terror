package com.d2rterror

import android.app.Application
import com.d2rterror.data.local.PreferencesManager
import com.d2rterror.di.appModule
import com.d2rterror.worker.WorkerScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class D2RTerrorApp : Application() {

    private val workerScheduler: WorkerScheduler by inject()
    private val preferencesManager: PreferencesManager by inject()

    override fun onCreate() {
        super.onCreate()

        // Initialize Koin
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@D2RTerrorApp)
            modules(appModule)
        }

        // Only schedule worker if notifications are enabled
        CoroutineScope(Dispatchers.IO).launch {
            val notificationsEnabled = preferencesManager.notificationsEnabled.first()
            if (notificationsEnabled) {
                workerScheduler.scheduleZoneCheck()
            }
        }
    }
}
