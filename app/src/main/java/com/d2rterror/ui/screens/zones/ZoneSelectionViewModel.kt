package com.d2rterror.ui.screens.zones

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d2rterror.data.local.PreferencesManager
import com.d2rterror.data.model.TerrorZone
import com.d2rterror.data.repository.TerrorZoneRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ZoneSelectionUiState(
    val zonesByAct: Map<Int, List<TerrorZone>> = emptyMap(),
    val selectedZoneIds: Set<Int> = emptySet(),
    val expandedActs: Set<Int> = setOf(1, 2, 3, 4, 5) // All expanded by default
)

class ZoneSelectionViewModel(
    private val repository: TerrorZoneRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _expandedActs = MutableStateFlow(setOf(1, 2, 3, 4, 5))

    val uiState: StateFlow<ZoneSelectionUiState> = combine(
        preferencesManager.selectedZones,
        _expandedActs
    ) { selectedZones, expandedActs ->
        ZoneSelectionUiState(
            zonesByAct = repository.getZonesByAct(),
            selectedZoneIds = selectedZones,
            expandedActs = expandedActs
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ZoneSelectionUiState(zonesByAct = repository.getZonesByAct())
    )

    fun toggleZone(zoneId: Int, isSelected: Boolean) {
        viewModelScope.launch {
            if (isSelected) {
                preferencesManager.addSelectedZone(zoneId)
            } else {
                preferencesManager.removeSelectedZone(zoneId)
            }
        }
    }

    fun selectAllInAct(act: Int) {
        viewModelScope.launch {
            val zonesInAct = repository.getZonesByAct()[act] ?: return@launch
            val currentSelected = uiState.value.selectedZoneIds.toMutableSet()
            zonesInAct.forEach { zone ->
                currentSelected.add(zone.id)
            }
            preferencesManager.setSelectedZones(currentSelected)
        }
    }

    fun clearAllInAct(act: Int) {
        viewModelScope.launch {
            val zonesInAct = repository.getZonesByAct()[act] ?: return@launch
            val zoneIdsInAct = zonesInAct.map { it.id }.toSet()
            val currentSelected = uiState.value.selectedZoneIds.toMutableSet()
            currentSelected.removeAll(zoneIdsInAct)
            preferencesManager.setSelectedZones(currentSelected)
        }
    }

    fun toggleActExpanded(act: Int) {
        _expandedActs.value = if (act in _expandedActs.value) {
            _expandedActs.value - act
        } else {
            _expandedActs.value + act
        }
    }

    fun selectAll() {
        viewModelScope.launch {
            val allZoneIds = repository.getAllZones().map { it.id }.toSet()
            preferencesManager.setSelectedZones(allZoneIds)
        }
    }

    fun clearAll() {
        viewModelScope.launch {
            preferencesManager.setSelectedZones(emptySet())
        }
    }
}
