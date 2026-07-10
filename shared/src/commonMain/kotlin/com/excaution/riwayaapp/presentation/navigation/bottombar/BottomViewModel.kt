package com.excaution.riwayaapp.presentation.navigation.bottombar

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BottomViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(BottomBarUiState())

    val uiState: StateFlow<BottomBarUiState> = _uiState.asStateFlow()

    fun selectBottom(index: Int) {
        _uiState.value = _uiState.value.copy(
                selectedBottomIndex = index
            )
    }
}