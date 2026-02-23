package com.d2rterror.data.api

data class TerrorZoneResponse(
    val current: List<ScrapedZone>,
    val next: List<ScrapedZone>
)

data class ScrapedZone(
    val displayName: String,  // Full name from website e.g. "Cathedral, Catacombs, Inner Cloister"
    val matchedIds: List<Int> // Matched zone IDs for notification filtering
)
