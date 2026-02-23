package com.d2rterror.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d2rterror.data.model.TerrorZoneState
import com.d2rterror.data.repository.TerrorZoneRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: TerrorZoneRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TerrorZoneState(isLoading = true))
    val uiState: StateFlow<TerrorZoneState> = _uiState.asStateFlow()

    init {
        loadTerrorZones()
    }

    fun loadTerrorZones() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            repository.getTerrorZones()
                .onSuccess { state ->
                    _uiState.value = state
                }
                .onFailure { exception ->
                    _uiState.value = TerrorZoneState(
                        isLoading = false,
                        error = exception.message ?: "Failed to load terror zones"
                    )
                }
        }
    }

    fun refresh() {
        loadTerrorZones()
    }
}
