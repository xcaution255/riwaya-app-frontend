package com.excaution.riwayaapp.presentation.auth.emailverify

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.excaution.riwayaapp.core.network.ApiResult
import com.excaution.riwayaapp.data.auth.AuthRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class VerifyEmailViewModel(private val repository: AuthRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(VerifyEmailUiState())
    val uiState = _uiState.asStateFlow()
    private val _events = Channel<VerifyEmailEvent>()
    val events = _events.receiveAsFlow()

    fun onOtpChange(v: String) = _uiState.update { it.copy(otp = v, errorMessage = null) }

    fun verify(email: String) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        when (val result = repository.verifyEmailOtp(email, _uiState.value.otp)) {
            is ApiResult.Success -> {
                _uiState.update { it.copy(isLoading = false) }
                _events.send(VerifyEmailEvent.NavigateToLogin)
            }
            is ApiResult.Error -> _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
            ApiResult.NetworkError -> _uiState.update { it.copy(isLoading = false, errorMessage = "No internet connection") }
        }
    }
}
data class VerifyEmailUiState(val otp: String = "", val isLoading: Boolean = false, val errorMessage: String? = null)
sealed class VerifyEmailEvent { data object NavigateToLogin : VerifyEmailEvent() }