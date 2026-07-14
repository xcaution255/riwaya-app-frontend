package com.excaution.riwayaapp.presentation.auth.login

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

class LoginViewModel(private val repository: AuthRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()
    private val _events = Channel<LoginEvent>()
    val events = _events.receiveAsFlow()

    fun onEmailChange(v: String) = _uiState.update { it.copy(email = v, errorMessage = null) }
    fun onPasswordChange(v: String) = _uiState.update { it.copy(password = v, errorMessage = null) }

    fun login() = viewModelScope.launch {
        val state = _uiState.value
        if (state.email.isBlank() || state.password.length < 8) {
            _uiState.update { it.copy(errorMessage = "Please fill all fields (password ≥ 8 chars)") }
            return@launch
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        when (val result = repository.login(state.email, state.password)) {
            is ApiResult.Success -> {
                _uiState.update { it.copy(isLoading = false) }
                _events.send(LoginEvent.NavigateToHome)
            }
            is ApiResult.Error -> _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
            ApiResult.NetworkError -> _uiState.update { it.copy(isLoading = false, errorMessage = "No internet connection") }
        }
    }

    fun apiStatus() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        when (val result = repository.apiStatus()) {
            is ApiResult.Success -> {
                _uiState.update { it.copy(isLoading = false, apiMessage = result.data.toString()) }
                _events.send(LoginEvent.NavigateToHome)
            }
            is ApiResult.Error ->
                _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
            ApiResult.NetworkError ->
                _uiState.update { it.copy(isLoading = false, errorMessage = "No internet connection") }
        }
    }
}
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val apiMessage: String? = null
    )
sealed class LoginEvent { data object NavigateToHome : LoginEvent() }