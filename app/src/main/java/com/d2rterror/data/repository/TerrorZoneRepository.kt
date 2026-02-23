package com.d2rterror.data.repository

import com.d2rterror.data.api.TerrorZoneScraper
import com.d2rterror.data.local.ZoneData
import com.d2rterror.data.model.TerrorZone
import com.d2rterror.data.model.TerrorZoneState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TerrorZoneRepository(
    private val scraper: TerrorZoneScraper
) {

    /**
     * Fetch current and next terror zones by scraping d2emu.com
     */
    suspend fun getTerrorZones(): Result<TerrorZoneState> = withContext(Dispatchers.IO) {
        try {
            val response = scraper.fetchTerrorZones()

            Result.success(
                TerrorZoneState(
                    current = response.current,
                    next = response.next,
                    isLoading = false,
                    error = null
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get all available terror zones (static data)
     */
    fun getAllZones(): List<TerrorZone> = ZoneData.getAllZones()

    /**
     * Get zones grouped by Act
     */
    fun getZonesByAct(): Map<Int, List<TerrorZone>> = ZoneData.getZonesByAct()

    /**
     * Get a specific zone by ID
     */
    fun getZoneById(id: Int): TerrorZone? = ZoneData.getZoneById(id)
}
