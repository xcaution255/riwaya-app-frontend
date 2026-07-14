package com.excaution.riwayaapp.presentation.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.excaution.riwayaapp.core.network.ApiResult
import com.excaution.riwayaapp.data.auth.AuthRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

data class RegisterUiState(
    val email: String = "",
    val password: String = "",
    val username: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

sealed class RegisterEvent {
    data class NavigateToVerifyEmail(val email: String) : RegisterEvent()
}

class RegisterViewModel(private val repository: AuthRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = Channel<RegisterEvent>()
    val events = _events.receiveAsFlow()

    fun onEmailChange(email: String) = _uiState.update { it.copy(email = email, errorMessage = null) }
    fun onPasswordChange(password: String) = _uiState.update { it.copy(password = password, errorMessage = null) }
    fun onNameChange(username: String) = _uiState.update { it.copy(username = username, errorMessage = null) }

    fun register() {
        val state = _uiState.value
        if (state.email.isBlank() || state.password.length < 8 || state.username.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please fill all fields (password ≥ 8 chars)") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = repository.register(state.email, state.password, state.username)) {
                is ApiResult.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _events.send(RegisterEvent.NavigateToVerifyEmail(state.email))
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