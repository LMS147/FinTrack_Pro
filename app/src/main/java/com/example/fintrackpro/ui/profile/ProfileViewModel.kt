package com.example.fintrackpro.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fintrackpro.data.entity.User
import com.example.fintrackpro.data.Repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val authRepository: AuthRepository,
    private val userId: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUser()
    }

    private fun loadUser() {
        viewModelScope.launch {
            val user = authRepository.getUserById(userId)
            _uiState.update { it.copy(user = user, isLoading = false) }
        }
    }

    fun updateCurrency(currency: String) {
        viewModelScope.launch {
            val currentUser = _uiState.value.user ?: return@launch
            val updated = currentUser.copy(defaultCurrency = currency)
            authRepository.updateUser(updated)
            _uiState.update { it.copy(user = updated) }
        }
    }

    fun updateProfile(displayName: String, email: String) {
        viewModelScope.launch {
            val currentUser = _uiState.value.user ?: return@launch
            val updated = currentUser.copy(displayName = displayName, email = email)
            authRepository.updateUser(updated)
            _uiState.update { it.copy(user = updated) }
        }
    }

    fun updateProfilePicture(uri: String) {
        viewModelScope.launch {
            val currentUser = _uiState.value.user ?: return@launch
            val updated = currentUser.copy(photoUrl = uri)
            authRepository.updateUser(updated)
            _uiState.update { it.copy(user = updated) }
        }
    }

    fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch {
            val currentUser = _uiState.value.user ?: return@launch
            val updated = currentUser.copy(notificationsEnabled = enabled)
            authRepository.updateUser(updated)
            _uiState.update { it.copy(user = updated) }
        }
    }

    fun toggleBiometrics(enabled: Boolean) {
        viewModelScope.launch {
            val currentUser = _uiState.value.user ?: return@launch
            val updated = currentUser.copy(biometricsEnabled = enabled)
            authRepository.updateUser(updated)
            _uiState.update { it.copy(user = updated) }
        }
    }

    data class ProfileUiState(
        val user: User? = null,
        val isLoading: Boolean = true
    )
}