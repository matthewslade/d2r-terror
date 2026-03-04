package com.d2rterror.data.repository

import com.d2rterror.data.api.TerrorZoneScraper
import com.d2rterror.data.local.ZoneData
import com.d2rterror.data.model.TerrorZone
import com.d2rterror.data.model.TerrorZoneState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class TerrorZoneRepository(
    private val scraper: TerrorZoneScraper
) {
    private val _terrorZoneState = MutableStateFlow(TerrorZoneState(isLoading = true))

    /**
     * Observable state of terror zones - single source of truth for UI and Worker
     */
    val terrorZoneState: StateFlow<TerrorZoneState> = _terrorZoneState.asStateFlow()

    /**
     * Refresh terror zone data from API and update the StateFlow.
     * Returns true if successful, false otherwise.
     */
    suspend fun refresh(): Boolean = withContext(Dispatchers.IO) {
        _terrorZoneState.value = _terrorZoneState.value.copy(isLoading = true, error = null)

        try {
            val response = scraper.fetchTerrorZones()
            _terrorZoneState.value = TerrorZoneState(
                current = response.current,
                next = response.next,
                isLoading = false,
                error = null,
                lastFetchTime = System.currentTimeMillis()
            )
            true
        } catch (e: Exception) {
            _terrorZoneState.value = TerrorZoneState(
                current = _terrorZoneState.value.current,
                next = _terrorZoneState.value.next,
                isLoading = false,
                error = e.message ?: "Failed to load terror zones"
            )
            false
        }
    }

    /**
     * Fetch current and next terror zones by scraping d2emu.com
     * @deprecated Use refresh() and observe terrorZoneState instead
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
