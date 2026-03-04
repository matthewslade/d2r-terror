package com.d2rterror.di

import com.d2rterror.data.api.TerrorZoneScraper
import com.d2rterror.data.local.PreferencesManager
import com.d2rterror.data.repository.TerrorZoneRepository
import com.d2rterror.notification.AlarmScheduler
import com.d2rterror.notification.NotificationHelper
import com.d2rterror.ui.screens.home.HomeViewModel
import com.d2rterror.ui.screens.settings.SettingsViewModel
import com.d2rterror.ui.screens.zones.ZoneSelectionViewModel
import com.d2rterror.worker.WorkerScheduler
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import java.util.concurrent.TimeUnit

val appModule = module {

    // OkHttp Client
    single {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    // Terror Zone Scraper
    single {
        TerrorZoneScraper(get())
    }

    // Preferences Manager
    single {
        PreferencesManager(androidContext())
    }

    // Repository
    single {
        TerrorZoneRepository(get())
    }

    // Notification Helper
    single {
        NotificationHelper(androidContext())
    }

    // Alarm Scheduler
    single {
        AlarmScheduler(androidContext())
    }

    // Worker Scheduler
    single {
        WorkerScheduler(androidContext())
    }

    // ViewModels
    viewModel {
        HomeViewModel(get())
    }

    viewModel {
        ZoneSelectionViewModel(get(), get())
    }

    viewModel {
        SettingsViewModel(get(), get())
    }
}
