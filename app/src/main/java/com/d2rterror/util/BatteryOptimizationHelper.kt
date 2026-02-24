package com.d2rterror.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings

object BatteryOptimizationHelper {

    /**
     * Check if the app is ignoring battery optimizations.
     */
    fun isIgnoringBatteryOptimizations(context: Context): Boolean {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return powerManager.isIgnoringBatteryOptimizations(context.packageName)
    }

    /**
     * Get an intent to request battery optimization exemption.
     * This opens a direct dialog asking the user to allow unrestricted battery usage.
     */
    fun getRequestIgnoreBatteryOptimizationsIntent(context: Context): Intent {
        return Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
            data = Uri.parse("package:${context.packageName}")
        }
    }

    /**
     * Get an intent to open battery optimization settings for all apps.
     * Useful if the direct request doesn't work on some devices.
     */
    fun getBatteryOptimizationSettingsIntent(): Intent {
        return Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
    }

    /**
     * Get an intent to open app-specific settings.
     * On some OEMs (Xiaomi, Huawei, etc.), users need to manually disable
     * battery restrictions in app settings.
     */
    fun getAppSettingsIntent(context: Context): Intent {
        return Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:${context.packageName}")
        }
    }

    /**
     * Check if we're on a device that might need extra battery optimization steps.
     * Chinese OEMs are notorious for aggressive battery management.
     */
    fun needsExtraBatterySteps(): Boolean {
        val manufacturer = Build.MANUFACTURER.lowercase()
        return manufacturer in listOf(
            "xiaomi", "redmi", "poco",
            "huawei", "honor",
            "oppo", "realme", "oneplus",
            "vivo", "iqoo",
            "samsung",
            "meizu",
            "asus"
        )
    }

    /**
     * Get manufacturer-specific instructions for disabling battery optimization.
     */
    fun getManufacturerInstructions(): String? {
        val manufacturer = Build.MANUFACTURER.lowercase()
        return when {
            manufacturer in listOf("xiaomi", "redmi", "poco") ->
                "Go to Settings > Apps > D2R Terror > Battery saver > No restrictions. " +
                "Also check Settings > Battery > App battery saver."

            manufacturer in listOf("huawei", "honor") ->
                "Go to Settings > Apps > D2R Terror > Battery > Launch manually, " +
                "and enable Auto-launch, Secondary launch, and Run in background."

            manufacturer in listOf("oppo", "realme", "oneplus") ->
                "Go to Settings > Battery > App battery management > D2R Terror > Allow background activity."

            manufacturer in listOf("vivo", "iqoo") ->
                "Go to Settings > Battery > High background power consumption > Add D2R Terror."

            manufacturer == "samsung" ->
                "Go to Settings > Apps > D2R Terror > Battery > Unrestricted. " +
                "Also disable 'Put app to sleep' in Device care > Battery."

            else -> null
        }
    }
}
