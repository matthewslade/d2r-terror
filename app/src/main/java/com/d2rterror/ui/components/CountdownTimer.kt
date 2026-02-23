package com.d2rterror.ui.components

import androidx.compose.runtime.*
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

/**
 * Calculates the countdown string until the next zone change.
 * Zones change at :00 and :30 of each hour.
 */
@Composable
fun rememberCountdownToNextZone(): String {
    var countdown by remember { mutableStateOf(calculateCountdown()) }

    LaunchedEffect(Unit) {
        while (true) {
            countdown = calculateCountdown()
            delay(1000) // Update every second
        }
    }

    return countdown
}

private fun calculateCountdown(): String {
    val now = LocalDateTime.now()
    val minute = now.minute
    val second = now.second

    // Next change is either at :00 or :30
    val minutesUntilChange = if (minute < 30) {
        30 - minute
    } else {
        60 - minute
    }

    val totalSeconds = (minutesUntilChange * 60) - second

    val mins = totalSeconds / 60
    val secs = totalSeconds % 60

    return String.format("%02d:%02d", mins, secs)
}

/**
 * Calculates minutes until the next zone change
 */
fun getMinutesUntilNextChange(): Int {
    val now = LocalDateTime.now()
    val minute = now.minute

    return if (minute < 30) {
        30 - minute
    } else {
        60 - minute
    }
}

/**
 * Gets the time when the next zone becomes active
 */
fun getNextZoneChangeTime(): LocalDateTime {
    val now = LocalDateTime.now()
    val minute = now.minute

    return if (minute < 30) {
        now.withMinute(30).withSecond(0).withNano(0)
    } else {
        now.plusHours(1).withMinute(0).withSecond(0).withNano(0)
    }
}
