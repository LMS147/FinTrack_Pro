package com.example.fintrackpro.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.fintrackpro.FinTrackApp
import com.example.fintrackpro.data.entity.UserEntity
import com.example.fintrackpro.data.Repository.UserRepository
import com.example.fintrackpro.utils.SessionManager
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepository: UserRepository = (application as FinTrackApp).userRepository
    private val sessionManager = SessionManager(application)

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Email and password are required")
            return
        }

        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val result = userRepository.loginUser(email, password)
            if (result.isSuccess) {
                val user = result.getOrNull()!!
                sessionManager.saveUserId(user.userId)
                _authState.value = AuthState.Success(user)
            } else {
                _authState.value = AuthState.Error(result.exceptionOrNull()?.message ?: "Login failed")
            }
        }
    }

    fun register(email: String, password: String, fullName: String) {
        if (email.isBlank() || password.isBlank() || fullName.isBlank()) {
            _authState.value = AuthState.Error("All fields are required")
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _authState.value = AuthState.Error("Invalid email format")
            return
        }

        if (password.length < 6) {
            _authState.value = AuthState.Error("Password must be at least 6 characters")
            return
        }

        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val result = userRepository.registerUser(email, password, fullName)
            if (result.isSuccess) {
                val user = result.getOrNull()!!
                sessionManager.saveUserId(user.userId)
                
                // Create a default account for the new user
                val defaultAccount = com.example.fintrackpro.data.entity.AccountEntity(
                    userId = user.userId,
                    accountName = "Main Wallet",
                    accountType = "CASH",
                    balance = 0.0,
                    currency = "ZAR",
                    color = "#2196F3"
                )
                (getApplication() as com.example.fintrackpro.FinTrackApp).accountRepository.insertAccount(defaultAccount)

                _authState.value = AuthState.Success(user)
            } else {
                _authState.value = AuthState.Error(result.exceptionOrNull()?.message ?: "Registration failed")
            }
        }
    }

    fun isLoggedIn(): Boolean = sessionManager.isLoggedIn()

    fun logout() {
        sessionManager.clearSession()
    }
}

sealed class AuthState {
    object Loading : AuthState()
    data class Success(val user: UserEntity) : AuthState()
    data class Error(val message: String) : AuthState()
}
