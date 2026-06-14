package com.example.fintrackpro.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fintrackpro.data.entity.UserEntity
import com.example.fintrackpro.data.Repository.UserRepository
import com.example.fintrackpro.utils.SessionManager
import com.example.fintrackpro.utils.ValidationUtils
import kotlinx.coroutines.launch

/**
 * ViewModel handling authentication logic for Login and Registration.
 */
class AuthViewModel(
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _loginState = MutableLiveData<LoginState>()
    val loginState: LiveData<LoginState> = _loginState

    private val _registerState = MutableLiveData<RegisterState>()
    val registerState: LiveData<RegisterState> = _registerState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading

            if (email.isBlank() || password.isBlank()) {
                _loginState.value = LoginState.Error("Email and password are required")
                return@launch
            }

            try {
                val result = userRepository.loginUser(email, password)

                if (result.isSuccess) {
                    val user = result.getOrNull()!!
                    sessionManager.saveUserId(user.userId)
                    _loginState.value = LoginState.Success(user)
                } else {
                    _loginState.value = LoginState.Error(result.exceptionOrNull()?.message ?: "Invalid email or password")
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error("Login failed: ${e.message}")
            }
        }
    }

    fun register(
        email: String,
        password: String,
        confirmPassword: String,
        fullName: String
    ) {
        viewModelScope.launch {
            _registerState.value = RegisterState.Loading

            // Simple validation; you can extend ValidationUtils if needed
            if (email.isBlank() || password.isBlank() || fullName.isBlank()) {
                _registerState.value = RegisterState.Error("All fields are required")
                return@launch
            }
            if (password != confirmPassword) {
                _registerState.value = RegisterState.Error("Passwords do not match")
                return@launch
            }

            try {
                val result = userRepository.registerUser(email, password, fullName)
                if (result.isSuccess) {
                    _registerState.value = RegisterState.Success(result.getOrNull()!!.userId)
                } else {
                    _registerState.value = RegisterState.Error(result.exceptionOrNull()?.message ?: "Registration failed")
                }
            } catch (e: Exception) {
                _registerState.value = RegisterState.Error("Registration error: ${e.message}")
            }
        }
    }

    fun resetLoginState() {
        _loginState.value = LoginState.Idle
    }

    fun resetRegisterState() {
        _registerState.value = RegisterState.Idle
    }
}

// Sealed classes – each branch is a valid state
sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val user: UserEntity) : LoginState()
    data class Error(val message: String) : LoginState()
}

sealed class RegisterState {
    object Idle : RegisterState()
    object Loading : RegisterState()
    data class Success(val userId: String) : RegisterState()
    data class Error(val message: String) : RegisterState()
}
