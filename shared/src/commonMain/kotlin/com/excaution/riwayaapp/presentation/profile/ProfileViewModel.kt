package com.excaution.riwayaapp.presentation.profile


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.excaution.riwayaapp.core.network.ApiResult
import com.excaution.riwayaapp.core.storage.TokenStorage
import com.excaution.riwayaapp.data.profile.ProfileRepository
import com.excaution.riwayaapp.data.profile.UpdateProfileResponse
import com.excaution.riwayaapp.data.profile.UserProfileResponse
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

class ProfileViewModel(
    private val repository: ProfileRepository,
    private val tokenStorage: TokenStorage) : ViewModel() {
    // Internal mutable state flow to manipulate state changes safely
    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Idle)
    // Public read-only stream consumed directly by the Compose Views
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()


    fun getProfile() = viewModelScope.launch {
        _uiState.value = ProfileUiState.Loading

        when (val result = repository.getProfile()) {
            is ApiResult.Success -> {
                _uiState.value = ProfileUiState.Success(data = result.data)
                //login false
            }
            is ApiResult.Error -> {
                _uiState.value = ProfileUiState.Error(message = result.message)}
            ApiResult.NetworkError -> _uiState.value = ProfileUiState.Error(message = "No internet connection")
        }
    }

    fun updateProfile(username: String, bio: String) = viewModelScope.launch {
        _uiState.value = ProfileUiState.Loading

        when (val result = repository.updateProfile(username, bio)) {
            is ApiResult.Success -> {
                getProfile()
                _uiState.value = ProfileUiState.UpdateSuccess(data = result.data)

            }
            is ApiResult.Error -> {
                // If the server returns a 401 or 403 error even after a refresh attempt,
                // the session is invalid. Force a logout.
                if (result.code == 401 || result.code == 403) {
                    tokenStorage.clearTokens()
                    // Emit a specific state to notify the UI to navigate back to the login screen
                    _uiState.value = ProfileUiState.Error("Session expired. Please log in again.")
                } else {
                    _uiState.value = ProfileUiState.Error(result.message)
                }
            }
            ApiResult.NetworkError -> _uiState.value = ProfileUiState.Error(message = "No internet connection")
        }
    }

    fun formatDate(isoString: String?): String {
        if (isoString.isNullOrBlank()) return "Recent Member"
        return try {
            // 1. Parse the incoming string payload directly into a Multiplatform Instant point
            val instant = Instant.parse(isoString)
            // 2. Localize the instant element according to the user's local operating system time zone configuration
            val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
            // 3. Format components cleanly with padded leading zeroes where necessary
            val day = localDateTime.day.toString().padStart(2, '0')
            val month = localDateTime.month.number.toString().padStart(2, '0')
            val year = localDateTime.year

            "$day/$month/$year"
        } catch (e: Exception) {
            // Fallback safety barrier prevents UI crashes if the network payload encounters layout anomalies
            "Recent Member"
        }
    }
}
sealed interface ProfileUiState {
    object Idle : ProfileUiState
    object Loading : ProfileUiState
    data class UpdateSuccess(val data: UpdateProfileResponse) : ProfileUiState
    data class Success(val data: UserProfileResponse) : ProfileUiState
    data class Error(val message: String) : ProfileUiState
}