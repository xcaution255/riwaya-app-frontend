package com.excaution.riwayaapp.presentation.auth.passwordrecovery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.excaution.riwayaapp.core.network.ApiResult
import com.excaution.riwayaapp.data.auth.AuthRepository
import com.excaution.riwayaapp.data.auth.ForgotPasswordResponse
import com.excaution.riwayaapp.data.auth.MessageResponse
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

data class ForgotPasswordUiState(
    val email: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed class ForgotPasswordEvent {
    data object NavigateToPasswordRecovery : ForgotPasswordEvent()
}

class ForgotPasswordViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = Channel<ForgotPasswordEvent>()
    val events = _events.receiveAsFlow()

    // fun to set the email when the screen loads
    fun initEmail(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun onEmailChange(v: String) = _uiState.update { it.copy(email = v, errorMessage = null) }

    fun forgotPassword(){
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = repository.forgotPassword(state.email)) {
                is ApiResult.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
                    when (result) {
                        ApiResult.Success(data = ForgotPasswordResponse("sent")) -> {
                            _events.send(ForgotPasswordEvent.NavigateToPasswordRecovery)
                        }
                        ApiResult.Success(data = ForgotPasswordResponse("not-found")) -> {
                            _uiState.update { it.copy(errorMessage = "Invalid email") }
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