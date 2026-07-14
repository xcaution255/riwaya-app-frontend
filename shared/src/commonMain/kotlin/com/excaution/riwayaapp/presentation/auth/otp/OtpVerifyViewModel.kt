package com.excaution.riwayaapp.presentation.auth.otp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.excaution.riwayaapp.core.network.ApiResult
import com.excaution.riwayaapp.data.auth.AuthRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

data class OtpVerifyUiState(
    val email: String = "",
    val otp: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

sealed class OtpVerifyEvent {
    data class NavigateToHome(val email: String) : OtpVerifyEvent()
}

class OtpVerifyViewModel(private val repository: AuthRepository) : ViewModel() {

    fun onOtpChange(otp: String) = _uiState.update { it.copy(otp = otp, errorMessage = null) }

    private val _uiState = MutableStateFlow(OtpVerifyUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = Channel<OtpVerifyEvent>()
    val events = _events.receiveAsFlow()


    fun verifyEmailOtp(){
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = repository.verifyEmailOtp(state.email, state.otp)) {
                is ApiResult.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _events.send(OtpVerifyEvent.NavigateToHome(state.email))
                }
                is ApiResult.Error -> _uiState.update {
                    it.copy(isLoading = false, errorMessage = result.message)
                }
                ApiResult.NetworkError -> _uiState.update {
                    it.copy(isLoading = false, errorMessage = "No internet connection")
                }
            }
        }
    }
}

// small helper used above
private fun <T> MutableStateFlow<T>.update(block: (T) -> T) { value = block(value) }