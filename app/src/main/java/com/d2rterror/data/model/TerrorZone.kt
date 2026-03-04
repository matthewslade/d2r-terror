package com.d2rterror.data.model

import com.d2rterror.data.api.ScrapedZone

data class TerrorZone(
    val id: Int,
    val name: String,
    val act: Int,
    val keywords: List<String> = emptyList()
) {
    val actDisplay: String
        get() = when (act) {
            1 -> "Act I"
            2 -> "Act II"
            3 -> "Act III"
            4 -> "Act IV"
            5 -> "Act V"
            else -> "Unknown"
        }
}

data class TerrorZoneState(
    val current: List<ScrapedZone> = emptyList(),
    val next: List<ScrapedZone> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val lastFetchTime: Long = 0L
) {
    /**
     * Check if data is stale by comparing fetch time with zone change boundaries.
     * Zones change at :00 and :30, so data is stale if we've crossed a boundary since last fetch.
     */
    fun isStale(): Boolean {
        if (lastFetchTime == 0L) return true

        val now = System.currentTimeMillis()
        val lastFetchMinute = (lastFetchTime / 60000) % 60
        val currentMinute = (now / 60000) % 60
        val lastFetchHour = (lastFetchTime / 3600000)
        val currentHour = (now / 3600000)

        // Different hour means we definitely crossed a boundary
        if (currentHour != lastFetchHour) return true

        // Same hour - check if we crossed :00 or :30
        val lastFetchPeriod = if (lastFetchMinute < 30) 0 else 1
        val currentPeriod = if (currentMinute < 30) 0 else 1

        return currentPeriod != lastFetchPeriod
    }
}
