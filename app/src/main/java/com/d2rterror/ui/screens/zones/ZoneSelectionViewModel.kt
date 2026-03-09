package com.d2rterror.ui.screens.zones

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d2rterror.data.local.PreferencesManager
import com.d2rterror.data.model.Element
import com.d2rterror.data.model.TerrorZoneGroup
import com.d2rterror.data.repository.TerrorZoneRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ZoneSelectionUiState(
    val zonesByAct: Map<Int, List<TerrorZoneGroup>> = emptyMap(),
    val selectedZoneIds: Set<Int> = emptySet(),
    val expandedActs: Set<Int> = setOf(1, 2, 3, 4, 5),
    val excludedImmunities: Set<Element> = emptySet(),
    val filteredZonesByAct: Map<Int, List<TerrorZoneGroup>> = emptyMap()
)

class ZoneSelectionViewModel(
    private val repository: TerrorZoneRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _expandedActs = MutableStateFlow(setOf(1, 2, 3, 4, 5))
    private val _excludedImmunities = MutableStateFlow<Set<Element>>(emptySet())

    val uiState: StateFlow<ZoneSelectionUiState> = combine(
        preferencesManager.selectedZones,
        _expandedActs,
        _excludedImmunities
    ) { selectedZones, expandedActs, excluded ->
        val allZonesByAct = repository.getZonesByAct()
        val filtered = if (excluded.isEmpty()) {
            allZonesByAct
        } else {
            allZonesByAct.mapValues { (_, zones) ->
                zones.filter { zone ->
                    // Keep zone if it does NOT have any of the excluded immunities
                    zone.immunities.none { it in excluded }
                }
            }.filterValues { it.isNotEmpty() }
        }
        ZoneSelectionUiState(
            zonesByAct = allZonesByAct,
            selectedZoneIds = selectedZones,
            expandedActs = expandedActs,
            excludedImmunities = excluded,
            filteredZonesByAct = filtered
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ZoneSelectionUiState(
            zonesByAct = repository.getZonesByAct(),
            filteredZonesByAct = repository.getZonesByAct()
        )
    )

    fun toggleImmunityFilter(element: Element) {
        _excludedImmunities.value = if (element in _excludedImmunities.value) {
            _excludedImmunities.value - element
        } else {
            _excludedImmunities.value + element
        }
    }

    fun clearFilters() {
        _excludedImmunities.value = emptySet()
    }

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
