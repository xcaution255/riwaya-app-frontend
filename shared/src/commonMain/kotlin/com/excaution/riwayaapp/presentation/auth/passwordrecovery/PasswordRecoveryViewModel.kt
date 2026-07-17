package com.excaution.riwayaapp.presentation.auth.passwordrecovery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.excaution.riwayaapp.core.network.ApiResult
import com.excaution.riwayaapp.data.auth.AuthRepository
import com.excaution.riwayaapp.data.auth.ForgotPasswordResponse
import com.excaution.riwayaapp.data.auth.ResetPasswordResponse
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

data class RecoveryPasswordUiState(
    val confPassword: String = "",
    val password: String = "",
    val otp: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed class RecoveryPasswordEvent {
    data object NavigateToLogin : RecoveryPasswordEvent()
}

class PasswordRecoveryViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(RecoveryPasswordUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = Channel<RecoveryPasswordEvent>()
    val events = _events.receiveAsFlow()


    fun onOtpChange(otp: String) = _uiState.update { it.copy(otp = otp, errorMessage = null) }
    fun onPasswordChange(pass: String) = _uiState.update { it.copy(password = pass, errorMessage = null) }
    fun onConformPasswordChange(confPass: String) = _uiState.update { it.copy(confPassword = confPass, errorMessage = null) }

    fun resetPassword(){
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = repository.resetPassword(state.otp, state.confPassword)) {
                is ApiResult.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
                    when (result) {
                        ApiResult.Success(data = ResetPasswordResponse("changed")) -> {
                            _events.send(RecoveryPasswordEvent.NavigateToLogin)
                        }
                        ApiResult.Success(data = ResetPasswordResponse("Invalid")) -> {
                            _uiState.update { it.copy(errorMessage = "Invalid OTP") }
                        }
                        else -> {
                            _uiState.update { it.copy(errorMessage = "Unknown error") }
                        }
                    }

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