package com.example.fintrackpro.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fintrackpro.data.entity.UserEntity
import com.example.fintrackpro.data.Repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val userId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUser()
    }

    private fun loadUser() {
        viewModelScope.launch {
            val user = userRepository.getUserById(userId)
            _uiState.update { it.copy(user = user, isLoading = false) }
        }
    }

    fun updateCurrency(currency: String) {
        viewModelScope.launch {
            val currentUser = _uiState.value.user ?: return@launch
            val updated = currentUser.copy(defaultCurrency = currency)
            userRepository.updateUser(updated)
            _uiState.update { it.copy(user = updated) }
        }
    }

    fun updateProfile(fullName: String, email: String) {
        viewModelScope.launch {
            val currentUser = _uiState.value.user ?: return@launch
            val updated = currentUser.copy(fullName = fullName, email = email)
            userRepository.updateUser(updated)
            _uiState.update { it.copy(user = updated) }
        }
    }

    fun updateProfilePicture(uri: String) {
        viewModelScope.launch {
            val currentUser = _uiState.value.user ?: return@launch
            val updated = currentUser.copy(profileImageUrl = uri)
            userRepository.updateUser(updated)
            _uiState.update { it.copy(user = updated) }
        }
    }

    data class ProfileUiState(
        val user: UserEntity? = null,
        val isLoading: Boolean = true
    )
}
