package com.d2rterror.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d2rterror.data.model.TerrorZoneState
import com.d2rterror.data.repository.TerrorZoneRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: TerrorZoneRepository
) : ViewModel() {

    val uiState: StateFlow<TerrorZoneState> = repository.terrorZoneState

    init {
        refreshIfStale()
        startStalenessChecker()
    }

    private fun refreshIfStale() {
        viewModelScope.launch {
            if (uiState.value.isStale()) {
                repository.refresh()
            }
        }
    }

    private fun startStalenessChecker() {
        viewModelScope.launch {
            while (isActive) {
                delay(30_000L) // Check every 30 seconds
                if (uiState.value.isStale() && !uiState.value.isLoading) {
                    repository.refresh()
                }
            }
        }
    }

    fun retry() {
        viewModelScope.launch {
            repository.refresh()
        }
    }
}
