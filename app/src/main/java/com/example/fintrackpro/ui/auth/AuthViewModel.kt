package com.example.fintrackpro.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fintrackpro.data.entity.User
import com.example.fintrackpro.data.Repository.AuthRepository
import com.example.fintrackpro.utils.SecurityUtils
import com.example.fintrackpro.utils.SessionManager
import com.example.fintrackpro.utils.ValidationUtils
import kotlinx.coroutines.launch

/**
 * ViewModel handling authentication logic for Login and Registration.
 */
class AuthViewModel(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _loginState = MutableLiveData<LoginState>()
    val loginState: LiveData<LoginState> = _loginState

    private val _registerState = MutableLiveData<RegisterState>()
    val registerState: LiveData<RegisterState> = _registerState

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading

            if (username.isBlank() || password.isBlank()) {
                _loginState.value = LoginState.Error("Username and password are required")
                return@launch
            }

            try {
                val passwordHash = SecurityUtils.hashPassword(password)
                val user = authRepository.login(username, passwordHash)

                if (user != null) {
                    authRepository.updateLastLogin(user.userId)
                    sessionManager.saveSession(user.userId)
                    _loginState.value = LoginState.Success(user)
                } else {
                    _loginState.value = LoginState.Error("Invalid username or password")
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error("Login failed: ${e.message}")
            }
        }
    }

    fun register(
        username: String,
        email: String,
        password: String,
        confirmPassword: String,
        displayName: String,
        defaultCurrency: String
    ) {
        viewModelScope.launch {
            _registerState.value = RegisterState.Loading

            val validationError = ValidationUtils.validateRegistration(
                username, email, password, confirmPassword, displayName
            )
            if (validationError != null) {
                _registerState.value = RegisterState.Error(validationError)
                return@launch
            }

            try {
                val existingUser = authRepository.getUserByUsername(username)
                if (existingUser != null) {
                    _registerState.value = RegisterState.Error("Username already taken")
                    return@launch
                }

                val newUser = User(
                    username = username,
                    email = email,
                    passwordHash = SecurityUtils.hashPassword(password),
                    displayName = displayName,
                    defaultCurrency = defaultCurrency,
                    createdAt = System.currentTimeMillis()
                )

                val userId = authRepository.register(newUser)
                if (userId > 0) {
                    _registerState.value = RegisterState.Success(userId)
                } else {
                    _registerState.value = RegisterState.Error("Registration failed")
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
    data class Success(val user: User) : LoginState()
    data class Error(val message: String) : LoginState()
}

sealed class RegisterState {
    object Idle : RegisterState()
    object Loading : RegisterState()
    data class Success(val userId: Long) : RegisterState()
    data class Error(val message: String) : RegisterState()
}