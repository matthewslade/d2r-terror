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
    val error: String? = null
)
